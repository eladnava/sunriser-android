package com.eladnava.sunriser.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.support.annotation.Nullable;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.scheduler.SunriseScheduler;
import com.eladnava.sunriser.utils.AppPreferences;


public class CheckSystemAlarm extends Service
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Log the event
        Log.d(Logging.TAG, "CheckSystemAlarm started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        // App enabled?
        if (AppPreferences.isAppEnabled(this))
        {
            // Reschedule sunrise alarm (without toast)
            SunriseScheduler.rescheduleSunriseAlarm(this, false);
        }
        else
        {
            // Kill the service
            stopSelf();
        }

        // Don't restart this service
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        // Write to log
        Log.d(Logging.TAG, "CheckSystemAlarm destroyed");

        // Nothing to do, destroy
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        // Don't allow binding to this service
        return null;
    }

}
