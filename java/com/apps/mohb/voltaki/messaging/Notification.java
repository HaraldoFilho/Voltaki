/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Notification.java
 *  Last modified : 7/15/16 7:16 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;
import com.apps.mohb.voltaki.button.ButtonCurrentState;
import com.apps.mohb.voltaki.button.ButtonEnums;
import com.apps.mohb.voltaki.button.ButtonStatus;


// This class manages the status bar notification

public class Notification {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private SharedPreferences sharedPref;


    public Notification() {
        // required empty constructor
    }

    public void startNotification(Intent intent, // intent that will be executed when notification is clicked
                                  Context context, String title, String text, int id) {

        // get icon preference from settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Set icon to "Colored"
        if (sharedPref.getString(Constants.STATUS_BAR_ICON, context.getString(R.string.set_status_bar_icon_default))
                .matches(context.getString(R.string.set_status_bar_icon_default))) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle(title)
                    .setContentText(text);
        } else // Set icon to "No color"
        if (sharedPref.getString(Constants.STATUS_BAR_ICON, context.getString(R.string.set_status_bar_icon_default))
                .matches(context.getString(R.string.set_status_bar_icon_grey))) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.bw_notification)
                    .setContentTitle(title)
                    .setContentText(text);
        }

        // register pending intent
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        Constants.INTENT_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // if notification is not disabled in settings shows the notification
        if (!sharedPref.getString(Constants.STATUS_BAR_ICON, context.getString(R.string.set_status_bar_icon_default))
                .matches(context.getString(R.string.set_status_bar_icon_disabled))) {
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setOngoing(true);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(id, mBuilder.build());
        }

    }

    public void changeNotificationIconColor(Context context, String color, int id) {
        // check button state before update notification
        if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                > ButtonEnums.convertEnumToInt(ButtonStatus.COME_BACK_HERE)) {

            // check if notification is "Colored" or "No color"
            if (color.matches(context.getString(R.string.set_status_bar_icon_default))) {
                mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_notification)
                        .setContentTitle(context.getString(R.string.set_title_status_bar_icon))
                        .setContentText(context.getString(R.string.notification_go_back));
            } else if (color.matches(context.getString(R.string.set_status_bar_icon_grey))) {
                mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.bw_notification)
                        .setContentTitle(context.getString(R.string.set_title_status_bar_icon))
                        .setContentText(context.getString(R.string.notification_go_back));
            }

            // register pending intent
            Intent intent = new Intent(context, GoBackNotificationActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            Constants.INTENT_REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            // shows the notification
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setOngoing(true);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(id, mBuilder.build());

        }
    }


    public void cancelNotification(Context context, int id) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

}

