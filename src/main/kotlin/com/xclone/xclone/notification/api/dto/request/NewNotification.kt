package com.xclone.xclone.notification.api.dto.request

data class NewNotification(
    val senderId: Int,
    val receiverId: Int,
    val referenceId: Int,
    val type: String,
    val text: String
)