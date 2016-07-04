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
import com.eladnava.sunriser.utils.SystemServices;
import com.eladnava.sunriser.utils.formatters.CountdownFormatter;

public class SunriseScheduler {
    public static void rescheduleSunriseAlarm(Context context, boolean showToast) {
        // Clear all previously-scheduled sunrise alarms
        SystemServices.getAlarmManager(context).cancel(getSunriseAlarmPendingIntent(context));

        // App disabled?
        if (!AppPreferences.isAppEnabled(context)) {
            // Don't reschedule any alarms
            return;
        }

        // Get current time (UTC)
        long now = System.currentTimeMillis();

        // Acquire next system alarm timestamp (in UTC)
        long nextAlarm = SystemClock.getNextAlarmTriggerTimestamp(context);

        // No alarm scheduled?
        if (nextAlarm == 0) {
            // Nothing to schedule, then
            return;
        }

        // Calculate when the sunrise alarm should commence (prior to the scheduled system alarm)
        long startSunrise = nextAlarm - (AppPreferences.getSunriseHeadstartMinutes(context) * 60 * 1000);

        // Sunrise should have started already? (If next alarm is scheduled within the head-start time)
        if (startSunrise < now) {
            // Don't schedule a sunrise alarm in the past
            return;
        }

        // Schedule the sunrise alarm by specifying the start time & pending intent to execute (the sunrise service)
        SystemServices.getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, startSunrise, getSunriseAlarmPendingIntent(context));

        // Get a human-readable countdown message to display and log
        String countdownMessage = CountdownFormatter.getAlarmCountdownText(startSunrise, context);

        // Alert the user (if invoked when the app is visible)
        if (showToast) {
            // Show a toast with countdown
            Toast.makeText(context, countdownMessage, Toast.LENGTH_LONG).show();
        }

        // Log the countdown
        Log.d(Logging.TAG, countdownMessage);
    }

    private static PendingIntent getSunriseAlarmPendingIntent(Context context) {
        // Set the intent to start our sunrise alarm service
        Intent intent = new Intent(context, SunriseAlarm.class);

        // Convert to pending intent
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
