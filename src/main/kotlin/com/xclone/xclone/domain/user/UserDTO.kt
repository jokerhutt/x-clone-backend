package com.xclone.xclone.domain.user

import java.sql.Timestamp

data class UserDTO(
    val id: Int?,
    val username: String,
    val email: String?,
    val bio: String?,
    val displayName: String,
    val posts: List<Int>,
    val bookmarkedPosts: List<Int>,
    val likedPosts: List<Int>,
    val followers: List<Int>,
    val following: List<Int>,
    val createdAt: Timestamp?,
    val replies: List<Int>,
    val retweets: List<Int>,
    val profilePictureUrl: String?,
    val bannerImageUrl: String?,
    val pinnedPostId: Int?,
    val verified: Boolean?
)