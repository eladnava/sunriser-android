package com.eladnava.sunriser.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.integrations.MiLightIntegration;
import com.eladnava.sunriser.utils.AppPreferences;

public class MoonlightService extends Service {
    AsyncMoonlightTask mAlarmTask;

    @Override
    public void onCreate() {
        super.onCreate();

        // Log startup
        Log.d(Logging.TAG, "MoonlightService started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The actual moonlight alarm logic (since we can't issue network calls on service's main thread)
        mAlarmTask = new AsyncMoonlightTask();

        // Run async task
        mAlarmTask.execute();

        // Don't restart this service
        return START_NOT_STICKY;
    }

    private void sendMoonlightCommands(Context context) throws Exception {
        // Get selected zone in settings
        int zone = AppPreferences.getMiLightZone(context);

        // Get the amount of milliseconds to sleep after the sunrise alarm reaches 100%
        int moonlightDuration = (AppPreferences.getMoonlightDurationMinutes(context) * 60 * 1000);

        // Turn on white light for the specified zone (in case the mode was set to RGB)
        MiLightIntegration.setWhiteModeByZone(zone, context);

        // First, set brightness level to 30% (should be enough for a night light) -- think about making this configurable via settings
        MiLightIntegration.setBrightnessByZone(30, zone, context);

        // Wait X amount of seconds before sending the white mode command
        Thread.sleep(MiLightIntegration.FADE_OUT_DURATION_MS);

        // Write to log
        Log.d(Logging.TAG, "Entering moonlight mode for " + moonlightDuration + "ms");

        // Wait X amount of milliseconds before turning the bulb off
        Thread.sleep(moonlightDuration);

        // Turn off the bulb after moonlight mode ends
        MiLightIntegration.fadeOutLightByZone(zone, context);
    }

    @Override
    public void onDestroy() {
        // Write to log
        Log.d(Logging.TAG, "MoonlightService destroyed");

        // Currently running the AsyncTask?
        if (mAlarmTask != null) {
            // Cancel (and interrupt any threads that are currently sleeping)
            mAlarmTask.cancel(true);
        }

        // Now we're ready to be destroyed
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Don't allow binding to this service
        return null;
    }

    public class AsyncMoonlightTask extends AsyncTask<Boolean, String, Integer> {
        @Override
        protected Integer doInBackground(Boolean... param) {
            try {
                // Run the main moonlight code (may be interrupted by AsyncTask.cancel())
                sendMoonlightCommands(MoonlightService.this);
            }
            catch (Exception exc) {
                // Log errors to logcat
                Log.e(Logging.TAG, "MoonlightService error", exc);
            }

            // Gotta return something
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            // All done with service, stop it now
            stopSelf();
        }
    }
}
