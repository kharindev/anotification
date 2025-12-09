package com.kharin.anotification;


import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;

import androidx.work.WorkManager;

import com.kharin.anotification.work.NotificationWorker;

import java.util.concurrent.TimeUnit;

public class ANotificationManager {

    public static final String TITLE_KEY = "title_key";
    public static final String MESSAGE_KEY = "message_key";
    public static final String CHANNEL_ID_KEY = "channel_id_key";
    public static final String REQUEST_CODE_KEY = "request_code_key";
    public static final String SMALL_ICON_INT_KEY = "small_icon_int_key";
    public static final String CLASS_KEY = "class_key";
    public static final String OPEN_PARAMETER_KEY = "open_parameter_key";
    private String channelId;

    public void createChannel(Context context, String channelId, String channelName, String description) {
        this.channelId = channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription(description);
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

            NotificationManager nm = context.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    public void scheduledNotification(Context context, ANotification notification) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        long delay = notification.hours * 3600000L
            + notification.minutes * 60000L
            + notification.seconds * 1000L;

        Data data = new Data.Builder()
            .putString(TITLE_KEY, notification.title)
            .putString(MESSAGE_KEY, notification.message)
            .putString(CHANNEL_ID_KEY, channelId)
            .putInt(REQUEST_CODE_KEY, notification.id)
            .putInt(SMALL_ICON_INT_KEY, notification.icon)
            .putString(OPEN_PARAMETER_KEY, notification.openParameter)
            .putString(CLASS_KEY, context.getClass().getName())
            .build();

        OneTimeWorkRequest request =
            new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
            "notify_" + notification.id,
            ExistingWorkPolicy.REPLACE,
            request
        );
    }

    public void cancelNotification(Context context, ANotification notification) {
        cancelNotification(context,notification.id);
    }

    public void cancelNotification(Context context, int code) {
        WorkManager.getInstance(context).cancelUniqueWork("notify_" + code);
        NotificationManagerCompat.from(context).cancel(code);
    }

    public static void showNotificationStatic(ANotificationSettings settings) {

        Intent intent = (settings.aClass != null)
            ? new Intent(settings.context, settings.aClass)
            : settings.context.getPackageManager()
            .getLaunchIntentForPackage(settings.context.getPackageName());

        if (intent == null)
            return;

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(OPEN_PARAMETER_KEY, settings.openParameter);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            flags |= PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pendingIntent = PendingIntent.getActivity(
            settings.context,
            settings.code,
            intent,
            flags
        );

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(settings.context, settings.channelId)
                .setSmallIcon(settings.smallIcon)
                .setContentTitle(settings.title)
                .setContentText(settings.message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(settings.context,
            Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(settings.context)
                .notify(settings.code, builder.build());
        }
    }

    public String getOpenParameter(Activity activity) {
        if (activity == null) return "nothing";

        String parameter = GetParameter(activity.getIntent(), OPEN_PARAMETER_KEY);
        return (parameter != null) ? parameter : "nothing";
    }

    public static String GetParameter(Intent intent, String key) {
        return (intent != null && intent.hasExtra(key))
            ? intent.getStringExtra(key)
            : "nothing";
    }

    public void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1000
                );
            }
        }
    }
}
