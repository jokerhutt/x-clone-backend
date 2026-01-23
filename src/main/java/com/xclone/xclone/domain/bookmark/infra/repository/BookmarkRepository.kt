package com.xclone.xclone.domain.bookmark.infra.repository

import com.xclone.xclone.domain.bookmark.Bookmark
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.util.Optional

interface BookmarkRepository : JpaRepository<Bookmark, Int> {

    override fun findById(id: Int): Optional<Bookmark>

    fun findAllByBookmarkedBy(id: Int): List<Bookmark>

    fun findAllByBookmarkedPost(bookmarkedPost: Int): List<Bookmark>

    fun existsByBookmarkedByAndBookmarkedPost(
        bookmarkedBy: Int,
        bookmarkedPost: Int
    ): Boolean

    fun findByBookmarkedByAndBookmarkedPost(
        bookmarkedBy: Int,
        bookmarkedPost: Int
    ): Optional<Bookmark>

    @Query(
        """
        select b.bookmarkedPost 
        from Bookmark b 
        where b.bookmarkedBy = :userId
        """
    )
    fun findAllBookmarkedPostIdsByUserId(@Param("userId") userId: Int): List<Int>

    @Query(
        """
        SELECT b.bookmarkedPost
        FROM Bookmark b
        JOIN Post p ON b.bookmarkedPost = p.id
        WHERE b.bookmarkedBy = :userId
          AND b.createdAt < :cursor
        ORDER BY b.createdAt DESC
        """
    )
    fun findPaginatedBookmarkedPostIdsByTime(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>
}