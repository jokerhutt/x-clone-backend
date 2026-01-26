package com.xclone.xclone.retweet.api.dto.request

data class RetweetRequest(
    val retweeterId: Int,
    val referenceId: Int,
    val type: String
)