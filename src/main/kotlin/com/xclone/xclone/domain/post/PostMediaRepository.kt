package com.xclone.xclone.domain.post

import org.springframework.data.jpa.repository.JpaRepository

interface PostMediaRepository : JpaRepository<PostMedia, Int> {

    fun findAllByPostId(postId: Int): ArrayList<PostMedia>

    fun findAllByPostIdIn(postIds: List<Int>): List<PostMedia>
}