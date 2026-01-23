package com.xclone.xclone.domain.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    Optional<User> findById(int id);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    @Query("""
    SELECT u FROM User u
    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<User> searchByUsernameOrDisplayName(@Param("query") String query);

    @Query(value = """
      SELECT u.id
      FROM users u
      LEFT JOIN follows f ON f.followed_id = u.id
      GROUP BY u.id
      HAVING COUNT(f.follower_id) <= :cursor
      ORDER BY COUNT(f.follower_id) DESC
      LIMIT :limit
  """, nativeQuery = true)
    List<Integer> findUserIdsByFollowerCount(@Param("cursor") long cursor, @Param("limit") int limit);

    @Query(value = """
      SELECT u.id
      FROM users u
      WHERE u.created_at < :cursor
      ORDER BY u.created_at DESC  
  """, nativeQuery = true)
    List<Integer> findUserIdsByCreatedAtCustom(@Param("cursor") Timestamp cursor,  Pageable pageable);


    Optional<User> findByGoogleId(String googleId);
}