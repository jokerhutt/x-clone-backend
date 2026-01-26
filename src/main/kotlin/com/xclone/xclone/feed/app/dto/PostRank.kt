package com.xclone.xclone.feed.app.dto

import com.xclone.xclone.post.domain.entity.Post

data class PostRank(
    val post: Post,
    var affinity: Double = 1.0,
    var weight: Double = 1.0,
    var timeDecay: Double = 0.0,
    var totalScore: Double = 0.0
) {
    fun computeTotalScore() {
        totalScore = affinity * weight * timeDecay
    }
}