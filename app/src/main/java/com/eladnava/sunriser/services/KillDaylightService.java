package com.eladnava.sunriser.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.utils.SingletonServices;

public class KillDaylightService extends IntentService {
    public KillDaylightService() {
        // Android...
        super("KillDaylightService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Write to log
        Log.d(Logging.TAG, "Killing daylight mode");

        try {
            // Turn off the bulb since we should have woken up by now
            SingletonServices.getMilightAPI(this).fadeOut();
        }
        catch (Exception exc) {
            // Log errors to logcat
            Log.e(Logging.TAG, "DaylightCompleted error", exc);
        }
    }
}
