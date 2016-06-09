package com.eladnava.sunriser.alarms;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import com.eladnava.sunriser.utils.SystemServices;

public class SystemClock
{
    @SuppressWarnings("deprecation")
    public static long getNextAlarmTriggerTimestamp(Context context)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            // API >= 21: use getNextAlarmClock()

            // Acquire an instance of the system-wide alarm manager
            AlarmManager alarmManager = SystemServices.getAlarmManager(context);

            // Query for next alarm info
            AlarmManager.AlarmClockInfo nextAlarm = alarmManager.getNextAlarmClock();

            // No scheduled alarm?
            if (nextAlarm == null) {
                return 0;
            }
            // Query for the next alarm and return its trigger timestamp (in millis)
            return nextAlarm.getTriggerTime();
        }
        else
        {
            // API < 21: Use NEXT_ALARM_FORMATTED

            // Get next scheduled alarm
            String nextAlarm = Settings.System.getString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);

            // No scheduled alarm?
            if ((nextAlarm == null) || ("".equals(nextAlarm))) {
                return 0;
            }

            // Get day, hour and minute for next alarm as a Date object
            Date alarmDate;
            String format = DateFormat.is24HourFormat(context) ? "E k:mm" : "E h:mm aa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault() );
            try {
                alarmDate = simpleDateFormat.parse(nextAlarm);
            }catch (ParseException e) {
                return 0;
            }

            // Convert alarm into Calendar object
            Calendar nextAlarmIncomplete = Calendar.getInstance();
            nextAlarmIncomplete.setTime(alarmDate);

            // Replace valid fields of nextAlarmIncomplete into nextAlarm set from current time
            Calendar nextAlarmCal = Calendar.getInstance();
            nextAlarmCal.set(Calendar.DAY_OF_WEEK, nextAlarmIncomplete.get(Calendar.DAY_OF_WEEK));
            nextAlarmCal.set(Calendar.HOUR_OF_DAY, nextAlarmIncomplete.get(Calendar.HOUR_OF_DAY));
            nextAlarmCal.set(Calendar.MINUTE, nextAlarmIncomplete.get(Calendar.MINUTE));
            nextAlarmCal.set(Calendar.SECOND, 0);

            // if the alarm is next week we have wrong date now (in the past). Adding 7 days should fix this
            if (nextAlarmCal.before(Calendar.getInstance())) {
                nextAlarmCal.add(Calendar.DATE, 7);
            }

            return nextAlarmCal.getTimeInMillis();
        }
    }
}
