package com.xclone.xclone.domain.retweet

data class NewRetweet(
    val retweeterId: Int,
    val referenceId: Int,
    val type: String
)
