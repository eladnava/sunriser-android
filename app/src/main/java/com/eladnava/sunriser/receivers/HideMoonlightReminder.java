package com.eladnava.sunriser.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.config.Notifications;
import com.eladnava.sunriser.utils.SystemServices;

public class HideMoonlightReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Log what we're doing
        Log.d(Logging.TAG, "Hiding moonlight reminder notification (if still displayed)");

        // Cancel moonlight reminder notification
        SystemServices.getNotificationManager(context).cancel(Notifications.MOONLIGHT_REMINDER_NOTIFICATION_ID);
    }
}
