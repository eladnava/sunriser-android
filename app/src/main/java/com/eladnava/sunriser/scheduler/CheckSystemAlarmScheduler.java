package com.eladnava.sunriser.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.services.CheckSystemAlarm;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.SystemServices;

public class CheckSystemAlarmScheduler
{
    // Check for modifications to alarm every CheckFrequencyHours hours
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

        long alarmFrequency = CheckFrequencyHours * AlarmManager.INTERVAL_HOUR;
        long nextAlarm = System.currentTimeMillis() + alarmFrequency;

        // Schedule the CheckSystemAlarm service
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextAlarm , alarmFrequency, getCheckSystemAlarmPendingIntent(context));
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarm , alarmFrequency, getCheckSystemAlarmPendingIntent(context));

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
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}
