package com.kharin.anotification.work;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;

import com.erow.dungeon.R;
import com.kharin.anotification.ANotificationManager;
import com.kharin.anotification.ANotificationSettings;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (ActivityCompat.checkSelfPermission(
            getApplicationContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure();
        }

        Data input = getInputData();

        String title = input.getString(ANotificationManager.TITLE_KEY);
        String message = input.getString(ANotificationManager.MESSAGE_KEY);
        String channelId = input.getString(ANotificationManager.CHANNEL_ID_KEY);
        int code = input.getInt(ANotificationManager.REQUEST_CODE_KEY, 0);
        int icon = input.getInt(ANotificationManager.SMALL_ICON_INT_KEY, R.drawable.notification);
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

        ANotificationManager.showNotificationStatic(settings);
        return Result.success();
    }
}
