package com.eladnava.sunriser.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eladnava.sunriser.R;
import com.eladnava.sunriser.activities.Main;
import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.config.Notifications;
import com.eladnava.sunriser.services.MoonlightService;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.Networking;
import com.eladnava.sunriser.utils.SingletonServices;

public class ShowMoonlightReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // App disabled or no Wi-Fi connection?
        if (!AppPreferences.isAppEnabled(context) || !Networking.isWiFiConnected(context)) {
            return;
        }

        // Get moonlight reminder hours preference
        int moonlightReminderHours = AppPreferences.getMoonlightReminderHours(context);

        // Reminder disabled?
        if (moonlightReminderHours < 1) {
            return;
        }

        // Build and style the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.mipmap.ic_sleep)
            .setVibrate(new long[]{0, 400, 250, 400})
            .setContentIntent(getMainActivityPendingIntent(context))
            .setContentTitle(context.getString(R.string.moonlight_reminder_notification))
            .setContentText(context.getString(R.string.moonlight_reminder_notification_desc))
            .addAction(R.mipmap.ic_moonlight, context.getString(R.string.moonlight_reminder_notification_action), getMoonlightServicePendingIntent(context));

        // Display notification
        SingletonServices.getNotificationManager(context).notify(Notifications.MOONLIGHT_REMINDER_NOTIFICATION_ID, mBuilder.build());

        // Hide it after half of the reminder hours pass (make this configurable?)
        long hideReminderTimestamp = System.currentTimeMillis() + (1000 * 60 * 60 * (moonlightReminderHours / 2));

        // Schedule it to be cancelled in the future
        SingletonServices.getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, hideReminderTimestamp, getHideReminderPendingIntent(context));

        // Log what we're doing
        Log.d(Logging.TAG, "Displaying moonlight reminder (attempt to hide it in " + ( ( hideReminderTimestamp - System.currentTimeMillis() ) / 1000 / 60 / 60 ) + " hours)");
    }

    private static PendingIntent getHideReminderPendingIntent(Context context) {
        // Set the intent to hide the moonlight reminder
        Intent intent = new Intent(context, HideMoonlightReminder.class);

        // Convert to pending intent
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static PendingIntent getMoonlightServicePendingIntent(Context context) {
        // Set the intent to start the moonlight service
        Intent intent = new Intent(context, MoonlightService.class);

        // Convert to pending intent
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private static PendingIntent getMainActivityPendingIntent(Context context) {
        // Set the intent to start the main activity
        Intent intent = new Intent(context, Main.class);

        // Convert to pending intent
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}
