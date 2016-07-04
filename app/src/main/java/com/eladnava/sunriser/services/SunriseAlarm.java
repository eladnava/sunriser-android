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
import com.eladnava.sunriser.utils.ThreadUtils;
import com.eladnava.sunriser.utils.intents.IntentExtras;

public class SunriseAlarm extends Service {
    private AsyncSunriseAlarm mAlarmTask;

    @Override
    public void onCreate() {
        super.onCreate();

        // Log the event
        Log.d(Logging.TAG, "SunriseAlarm started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // App enabled?
        if (AppPreferences.isAppEnabled(this)) {
            // Determine whether we are testing the alarm
            boolean testMode = intent.getBooleanExtra(IntentExtras.SUNRISE_ALARM_TEST, false);

            // Run the sunrise alarm async (since we can't issue network calls on service's main thread)
            mAlarmTask = new AsyncSunriseAlarm();

            // Start it
            mAlarmTask.execute(testMode);
        }
        else {
            // Kill the service
            stopSelf();
        }

        // Don't restart this service
        return START_NOT_STICKY;
    }

    private void sendSunriseAlarmCommands(boolean isTesting, Context context) throws Exception {
        // Get desired zone from app settings
        int zone = AppPreferences.getMiLightZone(context);

        // Get the amount of milliseconds to sleep in between each brightness percent increment
        int brightnessSleepInterval = (AppPreferences.getSunriseDurationMinutes(context) * 60 * 1000) / 100;

        // Get the amount of milliseconds to sleep after the sunrise alarm reaches 100%
        int daylightDuration = (AppPreferences.getDaylightDurationMinutes(context) * 60 * 1000);

        // Override values if testing (to emit a fast sunrise)
        if (isTesting) {
            // No sleep in between brightness levels
            brightnessSleepInterval = 0;

            // No sleep after 100%
            daylightDuration = 0;
        }

        // Take into account the sleep interval after sending the bulb selection command
        brightnessSleepInterval = (brightnessSleepInterval >= MiLightIntegration.SELECT_ZONE_DELAY_MS) ? brightnessSleepInterval - MiLightIntegration.SELECT_ZONE_DELAY_MS : 0;

        // First, set brightness level to 0% (to avoid a 100% full blast if that was the bulb's previous state before it was turned off)
        MiLightIntegration.setBrightnessByZone(0, zone, this);

        // Wait X amount of seconds before sending the white mode command
        ThreadUtils.sleepExact(MiLightIntegration.FADE_OUT_DURATION_MS);

        // Turn on white light for the specified zone (in case the mode was set to RGB)
        MiLightIntegration.setWhiteModeByZone(zone, this);

        // Write sleep interval to log
        Log.d(Logging.TAG, "Starting sunrise, delaying increments by " + brightnessSleepInterval + "ms");

        // Start incrementing the bulb's brightness
        for (int percent = 0; percent <= 100; percent++) {
            // Modify bulb brightness level to current percent value
            MiLightIntegration.setBrightnessByZone(percent, zone, this);

            // Wait X amount of seconds before incrementing brightness again, to satisfy the desired sunrise duration
            ThreadUtils.sleepExact(brightnessSleepInterval);
        }

        // Did the user enable "Daylight Forever"?
        if (AppPreferences.isDaylightForeverEnabled(context)) {
            // Write to log
            Log.d(Logging.TAG, "Entering daylight mode forever");
        }
        else {
            // Write to log
            Log.d(Logging.TAG, "Entering daylight mode for " + daylightDuration + "ms");

            // Wait X amount of milliseconds before turning the bulb off after the sunrise alarm reaches 100%
            ThreadUtils.sleepExact(daylightDuration);

            // Turn off the bulb since we should have woken up by now
            MiLightIntegration.fadeOutLightByZone(zone, this);
        }
    }

    @Override
    public void onDestroy() {
        // Write to log
        Log.d(Logging.TAG, "SunriseAlarm destroyed");

        // Currently running the sunrise alarm AsyncTask?
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

    public class AsyncSunriseAlarm extends AsyncTask<Boolean, String, Integer> {
        @Override
        protected Integer doInBackground(Boolean... testing) {
            try {
                // Run the main scheduling code (may be interrupted by AsyncTask.cancel())
                sendSunriseAlarmCommands(testing[0], SunriseAlarm.this);
            } catch (Exception exc) {
                // Log errors to logcat
                Log.e(Logging.TAG, "SunriseAlarm error", exc);
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
