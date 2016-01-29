package com.eladnava.sunriser.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

public class SystemServices
{
    private static AlarmManager mAlarmManager;
    private static SharedPreferences mSharedPreferences;
    private static ConnectivityManager mConnectivityManager;

    public static SharedPreferences getSharedPreferences(Context context)
    {
        // First time?
        if ( mSharedPreferences == null )
        {
            // Acquire system service
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        // Return cached instance
        return mSharedPreferences;
    }

    public static AlarmManager getAlarmManager(Context context)
    {
        // First time?
        if ( mAlarmManager == null )
        {
            // Acquire system service
            mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        }

        // Return cached instance
        return mAlarmManager;
    }

    public static ConnectivityManager getConnectivityManager(Context context)
    {
        // First time?
        if ( mConnectivityManager == null )
        {
            // Acquire system service
            mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        // Return cached instance
        return mConnectivityManager;
    }
}
