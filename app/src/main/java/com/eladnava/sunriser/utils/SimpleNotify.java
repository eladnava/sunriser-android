package com.eladnava.sunriser.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.eladnava.sunriser.R;

import java.util.Random;

public class SimpleNotify
{

    public static void notify( Context context, String title, String text)
    {
        notify(context, title, text, new Random().nextInt());
    }

    public static void notify( Context context, String title, String text, int notificationID)
    {
        if(false) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.drawable.ic_logo);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(text);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationID, mBuilder.build());
        }
    }
}
