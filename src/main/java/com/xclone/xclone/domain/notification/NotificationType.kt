package com.xclone.xclone.domain.notification

enum class NotificationType(val type: String) {
    LIKE("like"),
    REPOST("repost"),
    REPLY("reply"),
    MESSAGE("message"),
    FOLLOW("follow")
}