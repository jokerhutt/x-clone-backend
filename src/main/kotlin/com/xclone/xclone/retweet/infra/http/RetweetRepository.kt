package com.xclone.xclone.retweet.infra.http

import com.xclone.xclone.retweet.domain.entity.Retweet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RetweetRepository : JpaRepository<Retweet, Int> {

    fun findAllByRetweeterId(retweeterId: Int): List<Retweet>

    fun existsByRetweeterIdAndReferenceId(retweeterId: Int, referenceId: Int): Boolean

    fun findByRetweeterIdAndReferenceId(retweeterId: Int, referenceId: Int): Retweet?

    fun findAllByReferenceId(referenceId: Int): List<Retweet>

    @Query("select r.referenceId from Retweet r where r.retweeterId = :retweeterId")
    fun findAllReferenceIdsByRetweeterId(@Param("retweeterId") retweeterId: Int): List<Int>

    @Query("select r.retweeterId from Retweet r where r.referenceId = :postId")
    fun findAllRetweeterIdsByPostId(@Param("postId") postId: Int): List<Int>

}