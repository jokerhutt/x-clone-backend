package com.xclone.xclone.notification.api.dto.internal

import java.sql.Timestamp

data class NotificationDTO(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val referenceId: Int,
    val text: String,
    val type: String,
    val createdAt: Timestamp?,
    val seen: Boolean
)