package com.xclone.xclone.domain.notification

data class NewNotification(
    val senderId: Int,
    val receiverId: Int,
    val referenceId: Int,
    val type: String,
    val text: String
)