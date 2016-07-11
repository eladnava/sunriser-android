package com.eladnava.sunriser.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Networking {
    public static boolean isWiFiConnected(Context context) {
        // Get system connectivity manager
        ConnectivityManager cm = SingletonServices.getConnectivityManager(context);

        // Attempt to acquire active network info
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // May be null in case of no reception
        if (activeNetwork != null) {
            // Make sure we're connected via Wi-Fi
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // We're good!
                return true;
            }
        }

        // Not connected via Wi-Fi
        return false;
    }
}
