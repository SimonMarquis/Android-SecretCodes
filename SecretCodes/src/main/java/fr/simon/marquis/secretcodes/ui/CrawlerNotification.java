/*
 * Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.simon.marquis.secretcodes.ui;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import fr.simon.marquis.secretcodes.R;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.service.CrawlerService;

public class CrawlerNotification {
    private static final String NOTIFICATION_TAG = "CrawlerNotification";

    public static void notify(final Context context, final ArrayList<SecretCode> secretCodes, final int progress) {
        final Resources res = context.getResources();

        final String title = res.getString(R.string.crawler_notification_title);
        final String text = res.getString(R.string.crawler_notification_placeholder_text);

        Intent cancelIntent = new Intent(context, CrawlerService.class);
        cancelIntent.putExtra(CrawlerService.CANCEL_ACTION, true);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_crawler)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100, progress, progress == 0)
                .setTicker(title + "\n" + text)
                .setNumber(secretCodes.size())
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_action_cancel, res.getString(R.string.action_cancel),
                        PendingIntent.getService(context, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)).setAutoCancel(false);

        notify(context, builder.build());
    }

    private static void notify(final Context context, final Notification notification) {
        notification.flags |= Notification.FLAG_NO_CLEAR;
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}