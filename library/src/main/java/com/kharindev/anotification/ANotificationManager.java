package com.kharindev.anotification;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;
import com.kharindev.anotification.receiver.ANotificationReceiver;

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

        ANotificationLog.d("Creating channel: " + channelId);

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

            ANotificationLog.d("Channel created");
        }
    }

    public boolean hasPermission(Context context) {
        boolean need = needPermission();
        boolean granted = !need || ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

        ANotificationLog.d("hasPermission(): need=" + need + ", granted=" + granted);

        return granted;
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    public boolean needPermission(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public void scheduledNotification(Context context, ANotification notification) {
        ANotificationLog.d("Scheduling (AlarmManager): " + notification);

        if (!hasPermission(context)) {
            ANotificationLog.d("No permission → skip scheduling");
            return;
        }

        long delayMs =
                notification.hours * 3600000L +
                        notification.minutes * 60000L +
                        notification.seconds * 1000L;

        if (delayMs < 0) delayMs = 0;

        long triggerAt = System.currentTimeMillis() + delayMs;
        ANotificationLog.d("Delay = " + delayMs + " ms, triggerAt = " + triggerAt);

        Intent intent = new Intent(context, ANotificationReceiver.class);
        intent.putExtra(TITLE_KEY, notification.title);
        intent.putExtra(MESSAGE_KEY, notification.message);
        intent.putExtra(CHANNEL_ID_KEY, channelId);
        intent.putExtra(REQUEST_CODE_KEY, notification.id);
        intent.putExtra(SMALL_ICON_INT_KEY, notification.icon);
        intent.putExtra(OPEN_PARAMETER_KEY, notification.openParameter);
        intent.putExtra(CLASS_KEY, context.getClass().getName());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                notification.id,
                intent,
                flags
        );

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarm == null) {
            ANotificationLog.d("AlarmManager is null → cannot schedule");
            return;
        }

        // Современный, точный вариант с Doze
        alarm.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pi
        );

        ANotificationLog.d("Alarm scheduled (id=" + notification.id + ")");
    }

    public void cancelNotification(Context context, ANotification notification) {
        cancelNotification(context,notification.id);
    }

    public void cancelNotification(Context context, int code) {
        ANotificationLog.d("Cancel notification: " + code);
        WorkManager.getInstance(context).cancelUniqueWork("notify_" + code);
        NotificationManagerCompat.from(context).cancel(code);
        ANotificationLog.d("Cancelled");
    }

    public static void showNotificationStatic(ANotificationSettings settings) {

        Intent intent = (settings.aClass != null)
            ? new Intent(settings.context, settings.aClass)
            : settings.context.getPackageManager()
            .getLaunchIntentForPackage(settings.context.getPackageName());

        if (intent == null) {
            ANotificationLog.d("Intent null → return");
            return;
        }


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

        ANotificationLog.d("Notification sent (code=" + settings.code + ")");
    }

    public String getOpenParameter(Activity activity) {
        ANotificationLog.d("getOpenParameter() called");

        if (activity == null) {
            ANotificationLog.d("getOpenParameter(): activity == null → return 'nothing'");
            return "nothing";
        }

        Intent intent = activity.getIntent();
        ANotificationLog.d("getOpenParameter(): Intent = " + intent);

        String parameter = GetParameter(intent, OPEN_PARAMETER_KEY);

        ANotificationLog.d("getOpenParameter(): result = " + parameter);
        return parameter;
    }

    public static String GetParameter(Intent intent, String key) {
        if (intent == null) {
            ANotificationLog.d("GetParameter(): intent == null → return 'nothing'");
            return "nothing";
        }

        if (!intent.hasExtra(key)) {
            ANotificationLog.d("GetParameter(): no extra '" + key + "' → return 'nothing'");
            return "nothing";
        }

        String value = intent.getStringExtra(key);
        ANotificationLog.d("GetParameter(): '" + key + "' = " + value);

        return value != null ? value : "nothing";
    }

    public void requestPermission(Activity activity) {

        ANotificationLog.d("requestPermission() called");

        if (activity == null) {
            ANotificationLog.d("requestPermission(): activity == null → skip");
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ANotificationLog.d("requestPermission(): API < 33 → permission not required");
            return;
        }

        int state = activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS);
        boolean granted = state == PackageManager.PERMISSION_GRANTED;

        ANotificationLog.d("requestPermission(): granted=" + granted);

        if (!granted) {
            ANotificationLog.d("requestPermission(): requesting POST_NOTIFICATIONS");
            activity.requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1000
            );
        } else {
            ANotificationLog.d("requestPermission(): already granted → nothing to do");
        }
    }

}
