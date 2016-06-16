package com.eladnava.sunriser.services;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.support.annotation.Nullable;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.scheduler.SunriseScheduler;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.SimpleNotify;

public class CheckSystemAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Logging.TAG, "CheckSystemAlarm triggered");
        SimpleNotify.notify("CheckSystemAlarm","Alarm Triggered", context);

        // App enabled?
        if (AppPreferences.isAppEnabled(context))
        {
            // Reschedule sunrise alarm (without toast)
            SunriseScheduler.rescheduleSunriseAlarm(context, false);
        }
    }


}
