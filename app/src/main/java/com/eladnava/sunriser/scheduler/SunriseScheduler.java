package com.eladnava.sunriser.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.eladnava.sunriser.alarms.SystemClock;
import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.services.SunriseAlarm;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.SimpleNotify;
import com.eladnava.sunriser.utils.formatters.CountdownFormatter;
import com.eladnava.sunriser.utils.SystemServices;

public class SunriseScheduler
{
    public static void rescheduleSunriseAlarm(Context context, boolean showToast)
    {
        // Clear all previously-scheduled sunrise alarms
        SystemServices.getAlarmManager(context).cancel(getSunriseAlarmPendingIntent(context));

        // App disabled?
        if (!AppPreferences.isAppEnabled(context))
        {
            // Don't reschedule any alarms
            return;
        }

        // Acquire next system alarm timestamp (in UTC)
        long nextAlarm = SystemClock.getNextAlarmTriggerTimestamp(context);

        // No alarm scheduled?
        if ( nextAlarm == 0 )
        {
            SimpleNotify.notify("SunriseScheduler", "No alarm set", context);

            // Nothing to schedule
            return;
        }
        // Alarm has changed, or no previous alarm set. Set a new sunrise alarm

        // Get current time (UTC)
        long now = System.currentTimeMillis();

        // Calculate when the sunrise alarm should commence (prior to the scheduled system alarm)
        long startSunrise = nextAlarm - (AppPreferences.getSunriseHeadstartMinutes(context) * 60 * 1000);

        // Sunrise should have started already? (If next alarm is scheduled within the headstart time)
        // Allow alarms that should have triggered within the last 5 seconds, as we may be checking as the alarm is going off.
        if (startSunrise < (now-5000))
        {
            SimpleNotify.notify("SunriseScheduler", "Sunrise already happened", context);
            // Don't schedule a sunrise alarm in the past
            return;
        }

        // Schedule the sunrise alarm by specifying the start time & pending intent to execute (the sunrise service)
        SystemServices.getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, startSunrise, getSunriseAlarmPendingIntent(context));

        // Get a human-readable countdown message to display and log
        String countdownMessage = CountdownFormatter.getAlarmCountdownText(startSunrise, context);

        // Alert the user (if invoked when the app is visible)
        if (showToast)
        {
            // Show a toast with countdown
            Toast.makeText(context, countdownMessage, Toast.LENGTH_LONG).show();
        }

        SimpleNotify.notify("SunriseScheduler", countdownMessage, context);
        //SimpleNotify.notify("SunriseScheduler Set:", "Was "+nextAlarmOld+" now "+nextAlarm, context);

        // Log the countdown
        Log.d(Logging.TAG, countdownMessage);
    }


    private static PendingIntent getSunriseAlarmPendingIntent(Context context)
    {
        // Set the intent to start our sunrise alarm service
        Intent intent = new Intent(context, SunriseAlarm.class);

        // Convert to pending intent
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
