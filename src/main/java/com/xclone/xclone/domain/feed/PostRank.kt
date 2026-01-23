package com.xclone.xclone.domain.feed

import com.xclone.xclone.domain.post.Post
import com.xclone.xclone.domain.post.api.dto.PostDTO

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