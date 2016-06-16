package com.eladnava.sunriser.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.eladnava.sunriser.R;

public class SimpleNotify
{

    private static int notificationCounter = 0;
    public static void notify(String title, String text, Context context)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        //mBuilder.setSmallIcon(R.drawable.notification_icon);
        mBuilder.setSmallIcon(R.drawable.ic_logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationCounter, mBuilder.build());
        notificationCounter++;
    }

}
