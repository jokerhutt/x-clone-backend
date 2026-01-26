package com.xclone.xclone.post.api.dto.request

import org.springframework.web.multipart.MultipartFile

data class CreatePostRequest(
    val text: String?,
    val parentId: Int?,
    val images: List<MultipartFile>?,
    val pollChoices: List<String>?,
    val pollExpiry: List<String>?
)
