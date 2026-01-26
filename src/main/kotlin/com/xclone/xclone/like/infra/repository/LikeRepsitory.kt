package com.xclone.xclone.like.infra.repository

import com.xclone.xclone.like.domain.entity.Like
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.util.Optional

interface LikeRepository : JpaRepository<Like, Int> {

    override fun findById(id: Int): Optional<Like>

    fun findAllByLikerId(id: Int): List<Like>

    fun findAllByLikedPostId(id: Int): List<Like>

    @Query("select l.likedPostId from Like l where l.likerId = :likerId")
    fun findAllLikedPostIdsByLikerId(@Param("likerId") likerId: Int): List<Int>

    fun existsByLikerIdAndLikedPostId(likerId: Int, likedPostId: Int): Boolean

    fun findByLikerIdAndLikedPostId(likerId: Int, likedPostId: Int): Optional<Like>

    @Query(
        """
        SELECT l.likedPostId
        FROM Like l
        JOIN Post p ON l.likedPostId = p.id
        WHERE l.likerId = :userId
          AND l.createdAt < :cursor
        ORDER BY l.createdAt DESC
        """
    )
    fun findPaginatedLikedPostIdsByTime(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>
}