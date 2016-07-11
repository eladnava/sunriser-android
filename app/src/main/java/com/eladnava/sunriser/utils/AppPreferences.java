package com.eladnava.sunriser.utils;

import android.content.Context;

import com.eladnava.sunriser.R;

public class AppPreferences {
    public static boolean isAppEnabled(Context context) {
        // Return enabled/disabled flag from SharedPreferences (defaults to true)
        return SystemServices.getSharedPreferences(context).getBoolean(context.getString(R.string.enable_pref), context.getString(R.string.enable_pref_default) == "true");
    }

    public static int getMiLightZone(Context context) {
        // Get zone from SharedPreferences and convert to int
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.zone_pref), context.getString(R.string.zone_default)));
    }

    public static int getSunriseColor(Context context) {
        // Get color from SharedPreferences and convert to int
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.sunrise_color_pref), context.getString(R.string.sunrise_color_default)));
    }

    public static int getMoonlightColor(Context context) {
        // Get color from SharedPreferences and convert to int
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.moonlight_color_pref), context.getString(R.string.moonlight_color_default)));
    }

    public static boolean isDaylightForeverEnabled(Context context) {
        // Return enabled/disabled flag from SharedPreferences (defaults to true)
        return SystemServices.getSharedPreferences(context).getBoolean(context.getString(R.string.daylight_forever_pref), context.getString(R.string.daylight_forever_default) == "true");
    }

    public static int getDaylightDurationMinutes(Context context) {
        // Return daylight duration in minutes from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.daylight_duration_pref), context.getString(R.string.daylight_duration_default)));
    }

    public static int getMoonlightDurationMinutes(Context context) {
        // Return moonlight duration in minutes from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.moonlight_duration_pref), context.getString(R.string.moonlight_duration_default)));
    }

    public static int getMoonlightBrightnessLevel(Context context) {
        // Return moonlight brightness from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.moonlight_brightness_pref), context.getString(R.string.moonlight_brightness_default)));
    }

    public static int getMoonlightReminderHours(Context context) {
        // Return moonlight reminder in hours from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.moonlight_reminder_pref), context.getString(R.string.moonlight_reminder_default)));
    }

    public static int getSunriseDurationMinutes(Context context) {
        // Return sunrise duration in minutes from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.sunrise_duration_pref), context.getString(R.string.sunrise_duration_default)));
    }

    public static int getSunriseHeadstartMinutes(Context context) {
        // Return sunrise headstart in minutes from SharedPreferences
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.sunrise_headstart_pref), context.getString(R.string.sunrise_headstart_default)));
    }

    public static String getMiLightHost(Context context) {
        // Return MiLight IP address from SharedPreferences (defaults to 255.255.255.255)
        return SystemServices.getSharedPreferences(context).getString(context.getString(R.string.host_pref), context.getString(R.string.host_default));
    }

    public static int getMiLightPort(Context context) {
        // Return MiLight port from SharedPreferences (defaults to 8899)
        return Integer.parseInt(SystemServices.getSharedPreferences(context).getString(context.getString(R.string.port_pref), context.getString(R.string.host_default)));
    }
}

