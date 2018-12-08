/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.awesome.app.awesomeapp.util;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.awesome.app.awesomeapp.R;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "awesomenotification_channel_007";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);

        //NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
       //         getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT);
        //chan1.setLightColor(Color.GREEN);
        //chan1.setVibrationPattern(new long[]{ 0 });
        //chan1.enableVibration(true);
        //chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        //getManager().createNotificationChannel(chan1);

        //NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
        //        getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
        //chan2.setLightColor(Color.RED);
        //chan2.setVibrationPattern(new long[]{ 0 });
        //chan2.enableVibration(true);
        //chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        //getManager().createNotificationChannel(chan2);

        //NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.createNotificationChannel(chan2);
        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            String id = SECONDARY_CHANNEL;
            CharSequence name = getString(R.string.notification_channel_name);
            String desc = getString(R.string.notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setShowBadge(true);
           // channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + "raw/silent.ogg"),
           //         new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
           //                 .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
           //                 .build());
            channel.setDescription(desc);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotification2(String title, String body) {
        return new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return R.drawable.ic_vibration_black_24dp;
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}