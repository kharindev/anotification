package com.kharindev.anotification;

import android.util.Log;

public class ANotificationLog {

    private static boolean enabled = false;
    private static final String TAG = "ANotification";

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void d(String msg) {
        if (enabled) Log.d(TAG, msg);
    }

    public static void e(String msg, Throwable t) {
        if (enabled) Log.e(TAG, msg, t);
    }
}
