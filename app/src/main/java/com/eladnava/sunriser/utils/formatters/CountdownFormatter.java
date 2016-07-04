package com.eladnava.sunriser.utils.formatters;

import android.content.Context;

import com.eladnava.sunriser.R;

public class CountdownFormatter {
    public static String getAlarmCountdownText(long startTimestamp, Context context) {
        // Get current time in UTC
        long now = System.currentTimeMillis();

        // Calculate total minutes until sunrise alarm starts
        int totalMinutes = (int) ((startTimestamp - now) / 1000 / 60);

        // Calculate hours and minutes left
        int hoursLeft = totalMinutes / 60;
        int minutesLeft = totalMinutes % 60;

        // Prepare countdown text (supports plural and singular)
        String sunriseCountdown = hoursLeft + " hour" + ((hoursLeft != 1) ? "s" : "") + " and " + minutesLeft + " minute" + ((minutesLeft != 1) ? "s" : "");

        // Insert it into the sunrise countdown string
        return context.getString(R.string.sunrise_countdown, sunriseCountdown);
    }
}
