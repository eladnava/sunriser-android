package com.eladnava.sunriser.scheduler;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.services.CheckSystemAlarm;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.SystemServices;

public class CheckSystemAlarmScheduler
{
    // Check for modifications to alarm every HOURS_BETWEEN_CHECKS hours
    private static final int CheckFrequencyHours = 1;

    public static void scheduleCheckSystemAlarm(Context context)
    {
        // Clear all previously-scheduled CheckSystemAlarm alarms
        stopCheckSystemAlarm(context);

        // App disabled?
        if (!AppPreferences.isAppEnabled(context))
        {
            // Don't reschedule any alarms
            return;
        }

        // Get time for first run
        Calendar nextAlarmCal = Calendar.getInstance();
        nextAlarmCal.add(Calendar.HOUR, CheckFrequencyHours);
        nextAlarmCal.set(Calendar.MINUTE, 0);
        nextAlarmCal.set(Calendar.SECOND, 0);
        nextAlarmCal.set(Calendar.MILLISECOND, 0);

        // Schedule the CheckSystemAlarm service
        SystemServices.getAlarmManager(context).setInexactRepeating(AlarmManager.RTC_WAKEUP, nextAlarmCal.getTimeInMillis(), CheckFrequencyHours * AlarmManager.INTERVAL_HOUR, getCheckSystemAlarmPendingIntent(context));

        // Log the countdown
        Log.d(Logging.TAG, "CheckSystemAlarm service scheduled");
    }

    public static void stopCheckSystemAlarm(Context context)
    {
        // Clear all previously-scheduled CheckSystemAlarm alarms
        SystemServices.getAlarmManager(context).cancel(getCheckSystemAlarmPendingIntent(context));
    }


    private static PendingIntent getCheckSystemAlarmPendingIntent(Context context)
    {
        // Set the intent to start our sunrise alarm service
        Intent intent = new Intent(context, CheckSystemAlarm.class);

        // Convert to pending intent
        return PendingIntent.getService(context, 0, intent, 0);
    }

}
