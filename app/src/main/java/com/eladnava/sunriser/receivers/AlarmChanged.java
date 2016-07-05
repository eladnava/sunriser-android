package com.eladnava.sunriser.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.scheduler.SunriseScheduler;

public class AlarmChanged extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Make sure the right intent was provided
        if (intent.getAction().equals(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED)) {
            // Log alarm changed
            Log.d(Logging.TAG, "Alarm clock changed");

            // Reschedule sunrise alarm (without toast)
            SunriseScheduler.rescheduleSunriseAlarm(context, false);
        }
    }
}
