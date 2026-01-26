package com.xclone.xclone.post.api.dto.response

import java.sql.Timestamp

data class PostMediaDTO(
    val id: Int,
    val postId: Int,
    val fileName: String,
    val mimeType: String,
    val url: String,
    val storageKey: String,
    val createdAt: Timestamp
)