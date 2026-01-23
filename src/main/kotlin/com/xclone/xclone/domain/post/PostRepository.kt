package com.xclone.xclone.domain.post
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.awt.print.Pageable
import java.sql.Timestamp
import java.util.Optional

interface PostRepository : JpaRepository<Post, Int> {

    override fun findById(id: Int): Optional<Post>

    fun findByUserId(id: Int): Optional<Post>

    fun findAllByUserId(id: Int): Optional<List<Post>>

    fun findAllByParentId(id: Int?): ArrayList<Post>

    @Query(
        """
    SELECT p.id FROM Post p
    WHERE p.id IN (
        SELECT p1.id FROM Post p1
        WHERE p1.userId = :userId AND p1.parentId IS NULL

        UNION

        SELECT r.referenceId FROM Retweet r
        JOIN Post p2 ON p2.id = r.referenceId
        WHERE r.retweeterId = :userId AND p2.parentId IS NULL
    )
    AND p.id < :cursor
    ORDER BY p.id DESC
    """
    )
    fun findPaginatedTweetAndRetweetIdsByUserId(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>


    @Query(
        value = """
    SELECT post_id FROM (
      SELECT p.id AS post_id, p.created_at AS activity_time
      FROM posts p
      WHERE p.user_id = :userId
        AND p.parent_id IS NULL
        AND p.created_at <= :cursor

      UNION ALL

      SELECT r.reference_id AS post_id, r.created_at
      FROM retweets r
      JOIN posts p ON p.id = r.reference_id
      WHERE r.retweeter_id = :userId
        AND p.user_id != :userId
        AND r.created_at <= :cursor
    ) AS combined
    ORDER BY activity_time DESC
    """,
        nativeQuery = true
    )
    fun findPostIdsByUserAndReposts(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    @Query(
        """
    SELECT p.id
    FROM Post p
    WHERE p.parentId IS NULL AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
    """
    )
    fun findNextPaginatedPostIdsByTime(
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    @Query(
        "SELECT p.id FROM Post p WHERE p.userId = :userId AND p.parentId IS NULL AND p.id < :cursor ORDER BY p.id DESC"
    )
    fun findPaginatedTweetIdsByUserId(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Long,
        pageable: Pageable
    ): List<Int>

    @Query(
        "SELECT p FROM Post p WHERE p.userId = :userId AND p.parentId IS NULL AND p.id < :cursor ORDER BY p.id DESC"
    )
    fun findPaginatedTweetsByUserId(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Long,
        pageable: Pageable
    ): List<Post>

    @Query(
        """
    SELECT p.id
    FROM Post p
    WHERE p.userId = :userId
      AND p.parentId IS NOT NULL
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
    """
    )
    fun findPaginatedReplyIdsByUserIdByTime(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    @Query(
        """
    SELECT p.id
    FROM Post p
    WHERE p.userId IN :followedUserIds
      AND p.parentId IS NULL
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
    """
    )
    fun findPaginatedPostIdsFromFollowedUsersByTime(
        @Param("followedUserIds") followedUserIds: List<Int>,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    @Query("SELECT p.id FROM Post p")
    fun findAllPostIds(pageable: Pageable): Page<Int>

    @Query("SELECT p.id FROM Post p WHERE p.userId = :authorId AND p.parentId IS NULL")
    fun findPostIdsByAuthor(@Param("authorId") authorId: Int): List<Int>

    @Query("SELECT p FROM Post p WHERE p.parentId IS NULL")
    fun findAllTopLevelPosts(): List<Post>

    @Query(
        """
    SELECT p.id
    FROM Post p
    WHERE p.userId = :userId
      AND p.createdAt < :cursor
      AND EXISTS (
        SELECT 1 FROM PostMedia pm WHERE pm.postId = p.id
      )
    ORDER BY p.createdAt DESC
    """
    )
    fun findPaginatedPostIdsWithMediaByUserIdByTime(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>



}