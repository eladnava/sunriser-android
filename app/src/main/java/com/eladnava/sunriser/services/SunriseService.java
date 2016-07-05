package com.eladnava.sunriser.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.integrations.MiLightIntegration;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.Networking;
import com.eladnava.sunriser.utils.intents.IntentExtras;

import java.util.Timer;
import java.util.TimerTask;

public class SunriseService extends Service {
    Timer mSunriseTimer;
    Timer mDaylightTimer;

    boolean mIsTesting;
    int mCurrentBrightness;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create timers to handle future tasks
        mSunriseTimer = new Timer();
        mDaylightTimer = new Timer();

        // Log startup
        Log.d(Logging.TAG, "SunriseService started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // App disabled or no Wi-Fi connection?
        if (!AppPreferences.isAppEnabled(this) || !Networking.isWiFiConnected(this)) {
            stopSelf();
        }

        // Reset current brightness level
        mCurrentBrightness = 0;

        // Determine whether we are testing the alarm
        mIsTesting = intent.getBooleanExtra(IntentExtras.SUNRISE_ALARM_TEST, false);

        // Start the sunrise (schedule future brightness updates and daylight kill)
        mSunriseTimer.schedule(new StartSunriseTask(), 0);

        // Don't restart this service
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Write to log
        Log.d(Logging.TAG, "SunriseService destroyed");

        // Cancel any pending timers
        mSunriseTimer.cancel();
        mDaylightTimer.cancel();

        // Now we're ready to be destroyed
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Don't allow binding to this service
        return null;
    }

    private class StartSunriseTask extends TimerTask {
        @Override
        public void run() {
            try {
                // Write to log
                Log.d(Logging.TAG, "Starting sunrise");

                // Get desired zone from app settings
                int zone = AppPreferences.getMiLightZone(SunriseService.this);

                // Get the amount of milliseconds to wait in between each brightness increment
                int brightnessUpdateInterval = (AppPreferences.getSunriseDurationMinutes(SunriseService.this) * 60 * 1000) / 100;

                // Override interval if testing (to emit a fast sunrise)
                if (mIsTesting) {
                    brightnessUpdateInterval = 0;
                }

                // Take into account the update interval after sending the bulb selection command
                brightnessUpdateInterval = (brightnessUpdateInterval >= MiLightIntegration.SELECT_ZONE_DELAY_MS) ? brightnessUpdateInterval - MiLightIntegration.SELECT_ZONE_DELAY_MS : 0;

                // Make sure update interval is at least 50ms for timer interval to work correctly
                brightnessUpdateInterval = (brightnessUpdateInterval < 50) ? 50 : brightnessUpdateInterval;

                // Set brightness level to 0% and select the bulb (to avoid a 100% full blast if that was the bulb's previous state before it was turned off)
                MiLightIntegration.setBrightnessByZone(0, zone, SunriseService.this);

                // Wait X amount of seconds before sending the white mode command
                Thread.sleep(MiLightIntegration.FADE_OUT_DURATION_MS);

                // Get sunrise color from preferences
                int color = AppPreferences.getSunriseColor(SunriseService.this);

                // White mode?
                if (color == -1) {
                    // Turn on white light for the specified zone (in case the mode was set to RGB)
                    MiLightIntegration.setWhiteModeByZone(zone, SunriseService.this);
                }
                else {
                    // Set custom color (it's a decimal that can be converted to byte)
                    MiLightIntegration.setColorByZone(zone, color, SunriseService.this);
                }

                // Write update interval to log
                Log.d(Logging.TAG, "Starting sunrise, incrementing brightness every " + brightnessUpdateInterval + "ms");

                // Schedule the first brightness update
                mSunriseTimer.scheduleAtFixedRate(new UpdateBrightnessTask(), 0, brightnessUpdateInterval);
            }
            catch (Exception exc) {
                // Log errors to logcat
                Log.e(Logging.TAG, "StartSunriseTask error", exc);
            }
        }
    }

    private class UpdateBrightnessTask extends TimerTask {
        @Override
        public void run() {
            // Increment percentage
            int percent = ++mCurrentBrightness;

            try {
                // Get desired zone from app settings
                int zone = AppPreferences.getMiLightZone(SunriseService.this);

                // Modify bulb brightness level to current percent value
                MiLightIntegration.setBrightnessByZone(percent, zone, SunriseService.this);
            }
            catch (Exception exc) {
                // Log errors to logcat
                Log.e(Logging.TAG, "UpdateBrightnessTask error", exc);
            }

            // 100 percent?
            if (percent == 100) {
                // We're done with the timer
                mSunriseTimer.cancel();

                // Did the user enable "Daylight Forever"?
                if (AppPreferences.isDaylightForeverEnabled(SunriseService.this)) {
                    // Write to log
                    Log.d(Logging.TAG, "Entering daylight mode forever");

                    // All done with service, stop it now
                    stopSelf();
                    return;
                }

                // Get the amount of milliseconds to sleep after the sunrise alarm reaches 100%
                int daylightDuration = (AppPreferences.getDaylightDurationMinutes(SunriseService.this) * 60 * 1000);

                // Testing?
                if (mIsTesting) {
                    // Kill daylight after a moment
                    daylightDuration = 1500;
                }

                // Log daylight kill scheduling
                Log.d(Logging.TAG, "Scheduling daylight kill " + daylightDuration + "ms from now");

                // Set one-time RTC wake-up alarm to kill the daylight
                mDaylightTimer.schedule(new KillDaylightTask(), daylightDuration);
            }
        }
    }

    private class KillDaylightTask extends TimerTask {
        @Override
        public void run() {
            // Write to log
            Log.d(Logging.TAG, "Killing daylight mode");

            try {
                // Get desired zone from app settings
                int zone = AppPreferences.getMiLightZone(SunriseService.this);

                // Turn off the bulb since we should have woken up by now
                MiLightIntegration.fadeOutLightByZone(zone, SunriseService.this);
            }
            catch (Exception exc) {
                // Log errors to logcat
                Log.e(Logging.TAG, "KillDaylightTask error", exc);
            }

            // All done with service, stop it now
            stopSelf();
        }
    }
}
