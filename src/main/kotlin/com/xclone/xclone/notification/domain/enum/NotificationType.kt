package com.xclone.xclone.notification.domain.enum

enum class NotificationType(val type: String) {
    LIKE("like"),
    REPOST("repost"),
    REPLY("reply"),
    MESSAGE("message"),
    FOLLOW("follow")
}