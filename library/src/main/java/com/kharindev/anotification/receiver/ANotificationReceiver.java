package com.kharindev.anotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kharindev.anotification.ANotificationLog;
import com.kharindev.anotification.ANotificationManager;
import com.kharindev.anotification.ANotificationSettings;

public class ANotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ANotificationLog.d("NotificationReceiver.onReceive()");

        String title = intent.getStringExtra(ANotificationManager.TITLE_KEY);
        String message = intent.getStringExtra(ANotificationManager.MESSAGE_KEY);
        String channelId = intent.getStringExtra(ANotificationManager.CHANNEL_ID_KEY);
        int code = intent.getIntExtra(ANotificationManager.REQUEST_CODE_KEY, 0);
        int icon = intent.getIntExtra(ANotificationManager.SMALL_ICON_INT_KEY, -1);
        String openParameter = intent.getStringExtra(ANotificationManager.OPEN_PARAMETER_KEY);
        String className = intent.getStringExtra(ANotificationManager.CLASS_KEY);

        ANotificationSettings settings = new ANotificationSettings(context)
                .withTitle(title)
                .withMessage(message)
                .withChannelId(channelId)
                .withCode(code)
                .withIcon(icon)
                .withOpenParameter(openParameter)
                .withActivityClass(className);

        ANotificationManager.showNotificationStatic(settings);
    }
}