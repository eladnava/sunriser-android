package com.eladnava.sunriser.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.scheduler.SunriseScheduler;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Make sure the right intent was provided
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Log the event
            Log.d(Logging.TAG, "Boot completed");

            // Reschedule sunrise alarm (without toast)
            SunriseScheduler.rescheduleSunriseAlarm(context, false);
        }
    }
}
