package com.xclone.xclone.post.infra.http

import com.xclone.xclone.post.domain.entity.PostMedia
import org.springframework.data.jpa.repository.JpaRepository

interface PostMediaRepository : JpaRepository<PostMedia, Int> {

    fun findAllByPostId(postId: Int): ArrayList<PostMedia>

    fun findAllByPostIdIn(postIds: List<Int>): List<PostMedia>
}