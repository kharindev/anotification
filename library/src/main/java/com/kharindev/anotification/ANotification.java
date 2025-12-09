package com.kharin.anotification;

import com.erow.dungeon.R;

public class ANotification {

    public int id;
    public String title;
    public String message;
    public int hours;
    public int minutes;
    public int seconds;
    public String openParameter = "nothing";
    public int icon = -1;

    public ANotification(int id) {
        this.id = id;
    }

    public ANotification withTitle(String title) {
        this.title = title;
        return this;
    }

    public ANotification withMessage(String message) {
        this.message = message;
        return this;
    }

    public ANotification withIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public ANotification withDefaultIcon() {
        this.icon = R.drawable.notification;
        return this;
    }

    public ANotification withOpenParameter(String openParameter) {
        this.openParameter = (openParameter != null) ? openParameter : "empty";
        return this;
    }

    public ANotification withOpenNothing() {
        this.openParameter = "nothing";
        return this;
    }

    public ANotification withHours(int hours) {
        this.hours = Math.max(hours, 0);
        return this;
    }

    public ANotification withMinutes(int minutes) {
        this.minutes = Math.max(minutes, 0);
        return this;
    }

    public ANotification withSeconds(int seconds) {
        this.seconds = Math.max(seconds, 0);
        return this;
    }

    @Override
    public String toString() {
        return "ANotification{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", message='" + message + '\'' +
            ", hours=" + hours +
            ", minutes=" + minutes +
            ", seconds=" + seconds +
            ", openParameter='" + openParameter + '\'' +
            ", icon=" + icon +
            '}';
    }
}
