package com.xclone.xclone.user.infra.http

import com.xclone.xclone.user.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.util.Optional

interface UserRepository : JpaRepository<User, Int> {

    fun findByUsername(username: String): User?

    override fun findById(id: Int): Optional<User>

    fun existsUserByEmail(email: String): Boolean

    fun existsUserByUsername(username: String): Boolean

    @Query(
        """
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
        """
    )
    fun searchByUsernameOrDisplayName(@Param("query") query: String): List<User>

    @Query(
        value = """
          SELECT u.id
          FROM users u
          LEFT JOIN follows f ON f.followed_id = u.id
          GROUP BY u.id
          HAVING COUNT(f.follower_id) <= :cursor
          ORDER BY COUNT(f.follower_id) DESC
          LIMIT :limit
        """,
        nativeQuery = true
    )
    fun findUserIdsByFollowerCount(
        @Param("cursor") cursor: Long,
        @Param("limit") limit: Int
    ): List<Int>

    @Query(
        value = """
          SELECT u.id
          FROM users u
          WHERE u.created_at < :cursor
          ORDER BY u.created_at DESC
        """,
        nativeQuery = true
    )
    fun findUserIdsByCreatedAtCustom(
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    fun findByGoogleId(googleId: String): Optional<User>
}