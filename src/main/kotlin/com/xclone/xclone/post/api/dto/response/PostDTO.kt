package com.xclone.xclone.post.api.dto.response

import java.sql.Timestamp

data class PostDTO(
    val id: Int?,
    val userId: Int?,
    val text: String?,
    val createdAt: Timestamp?,
    val likedBy: List<Int>,
    val bookmarkedBy: List<Int>,
    val replies: List<Int>,
    val parentId: Int?,
    val retweetedBy: List<Int>,
    val postMedia: List<PostMediaDTO>,
    val pollId: Int?,
    val pollExpiryTimeStamp: Timestamp?
)