package com.kharindev.anotification.work;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;

import com.kharindev.anotification.ANotificationLog;
import com.kharindev.anotification.ANotificationManager;
import com.kharindev.anotification.ANotificationSettings;


public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        ANotificationLog.d("Worker created");
    }

    @NonNull
    @Override
    public Result doWork() {
        ANotificationLog.d("Worker started");
        if (ActivityCompat.checkSelfPermission(
            getApplicationContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            ANotificationLog.d("Permission denied → failure");
            return Result.failure();
        }

        Data input = getInputData();

        ANotificationLog.d("InputData: " + input.toString());
        try {
            String title = input.getString(ANotificationManager.TITLE_KEY);
            String message = input.getString(ANotificationManager.MESSAGE_KEY);
            String channelId = input.getString(ANotificationManager.CHANNEL_ID_KEY);
            int code = input.getInt(ANotificationManager.REQUEST_CODE_KEY, 0);
            int icon = input.getInt(ANotificationManager.SMALL_ICON_INT_KEY, -1);
            String open = input.getString(ANotificationManager.OPEN_PARAMETER_KEY);
            String className = input.getString(ANotificationManager.CLASS_KEY);

            ANotificationSettings settings = new ANotificationSettings(getApplicationContext())
                .withTitle(title)
                .withMessage(message)
                .withChannelId(channelId)
                .withCode(code)
                .withIcon(icon)
                .withOpenParameter(open)
                .withActivityClass(className);

            ANotificationLog.d("Showing notification…");
            ANotificationManager.showNotificationStatic(settings);
            ANotificationLog.d("Success");
            return Result.success();
        } catch (Exception e) {
            ANotificationLog.e("Exception in worker", e);
            return Result.failure();
        }
    }
}
