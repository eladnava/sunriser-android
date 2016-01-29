package com.eladnava.sunriser.alarms;

import android.app.AlarmManager;
import android.content.Context;

import com.eladnava.sunriser.utils.SystemServices;

public class SystemClock
{
    public static long getNextAlarmTriggerTimestamp(Context context)
    {
        // Acquire an instance of the system-wide alarm manager
        AlarmManager alarmManager = SystemServices.getAlarmManager(context);

        // Query for next alarm info
        AlarmManager.AlarmClockInfo nextAlarm = alarmManager.getNextAlarmClock();

        // No scheduled alarm?
        if ( nextAlarm == null )
        {
            return 0;
        }

        // Query for the next alarm and return its trigger timestamp (in millis)
        return nextAlarm.getTriggerTime();
    }
}
