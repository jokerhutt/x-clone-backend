package com.xclone.xclone.feed.infra.repository

import com.xclone.xclone.feed.domain.entity.FeedEntry
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FeedEntryRepository : JpaRepository<FeedEntry, Int> {

    fun findByUserIdOrderByPositionAsc(userId: Int): List<FeedEntry>

    @Query(
        """
        SELECT f.postId
        FROM FeedEntry f
        WHERE f.userId = :userId AND f.position >= :cursor
        ORDER BY f.position ASC
        """
    )
    fun getFeedPostIdsCustom(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Long,
        pageable: Pageable
    ): List<Int>

    fun deleteByUserId(userId: Int)

    fun findAllByUserId(userId: Int): List<FeedEntry>

    fun findByPostId(postId: Int): FeedEntry?

    fun findByPostIdAndUserId(postId: Int, userId: Int): FeedEntry?
}