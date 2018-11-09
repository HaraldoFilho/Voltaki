/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Notification.java
 *  Last modified : 11/8/18 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


// This class manages the status bar notification

public class Notification {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public Notification() {
        // required empty constructor
    }

    public void startNotification(Intent intent, // intent that will be executed when notification is clicked
                                  Context context, String title, String text, int id) {

        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text);

        // register pending intent
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        Constants.INTENT_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());

    }

    public void cancelNotification(Context context, int id) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    public void startGoBackNotification(Context context) {
        // intent that will open Google Maps when notification is clicked
        Intent intent = new Intent(context, GoBackNotificationActivity.class);
        // show notification
        startNotification(intent, context, context.getString(R.string.info_app_name),
                context.getString(R.string.notification_go_back), Constants.NOTIFICATION_ID);
    }

}

