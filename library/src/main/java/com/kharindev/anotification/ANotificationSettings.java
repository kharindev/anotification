package com.kharindev.anotification;

import android.content.Context;

import androidx.annotation.NonNull;

public class ANotificationSettings {

    public Context context;
    public Class<?> aClass;
    public String title;
    public String message;
    public String channelId;
    public String openParameter = "nothing";
    public int smallIcon = -1;
    public int code;

    public ANotificationSettings(Context context) {
        this.context = context;
    }

    public ANotificationSettings withTitle(String title) {
        this.title = title;
        return this;
    }

    public ANotificationSettings withMessage(String message) {
        this.message = message;
        return this;
    }

    public ANotificationSettings withChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public ANotificationSettings withCode(int code) {
        this.code = code;
        return this;
    }

    public ANotificationSettings withIcon(int icon) {
        if (icon >= 0)
            this.smallIcon = icon;
        return this;
    }

    public ANotificationSettings withOpenParameter(String openParameter) {
        this.openParameter = (openParameter != null) ? openParameter : "nothing";
        return this;
    }

    public ANotificationSettings withActivityClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            this.aClass = null;
            return this;
        }
        try {
            this.aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            this.aClass = null;
        }
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationSettings{" +
            "context=" + context +
            ", title='" + title + '\'' +
            ", message='" + message + '\'' +
            ", channelId='" + channelId + '\'' +
            ", smallIcon=" + smallIcon +
            ", code=" + code +
            ", aClass=" + aClass +
            '}';
    }
}
