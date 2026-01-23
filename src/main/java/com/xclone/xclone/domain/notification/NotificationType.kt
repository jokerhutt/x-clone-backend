package com.xclone.xclone.domain.notification;


public enum NotificationType {
    LIKE("like"),
    REPOST("repost"),
    REPLY("reply"),
    MESSAGE("message"),
    FOLLOW("follow");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
