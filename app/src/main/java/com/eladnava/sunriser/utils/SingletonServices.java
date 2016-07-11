package com.eladnava.sunriser.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eladnava.milight.api.MilightAPI;
import com.eladnava.sunriser.config.Logging;

public class SingletonServices {
    private static MilightAPI mMilightAPI;
    private static AlarmManager mAlarmManager;
    private static SharedPreferences mSharedPreferences;
    private static ConnectivityManager mConnectivityManager;
    private static NotificationManager mNotificationManager;

    public static void resetMilightAPI() {
        // Forget the current instance of the API (in case the router address / zone changed)
        mMilightAPI = null;
    }

    public static MilightAPI getMilightAPI(Context context) {
        // First time?
        if (mMilightAPI == null) {
            try {
                // Instantiate a new instance of the API client
                mMilightAPI = new MilightAPI(context, AppPreferences.getMiLightHost(context), AppPreferences.getMiLightPort(context), AppPreferences.getMiLightZone(context));
            }
            catch(Exception err) {
                // Log to console and return null (this should never occur since we always provide a valid zone integer)
                Log.e(Logging.TAG, "Milight API instantiation failed", err);
            }
        }

        // Return cached instance
        return mMilightAPI;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        // First time?
        if (mSharedPreferences == null) {
            // Acquire system service
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        // Return cached instance
        return mSharedPreferences;
    }

    public static AlarmManager getAlarmManager(Context context) {
        // First time?
        if (mAlarmManager == null) {
            // Acquire system service
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        // Return cached instance
        return mAlarmManager;
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        // First time?
        if (mConnectivityManager == null) {
            // Acquire system service
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        // Return cached instance
        return mConnectivityManager;
    }

    public static NotificationManager getNotificationManager(Context context) {
        // First time?
        if (mNotificationManager == null) {
            // Acquire system service
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Return cached instance
        return mNotificationManager;
    }
}
