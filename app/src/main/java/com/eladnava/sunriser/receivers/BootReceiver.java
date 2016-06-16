package com.eladnava.sunriser.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.scheduler.SunriseScheduler;
import com.eladnava.sunriser.services.CheckSystemAlarm;

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Make sure the right intent was provided
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            // Log the event
            Log.d(Logging.TAG, "Boot completed");

            // Reschedule sunrise alarm (without toast)
            SunriseScheduler.rescheduleSunriseAlarm(context, false);

            // For API<21: Schedule CheckSystemAlarm
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CheckSystemAlarm.scheduleCheckSystemAlarm(context);
            }
        }
    }
}
