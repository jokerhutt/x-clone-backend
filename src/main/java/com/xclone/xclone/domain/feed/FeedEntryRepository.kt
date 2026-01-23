package com.xclone.xclone.domain.feed;

import com.xclone.xclone.domain.post.PostMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedEntryRepository extends JpaRepository<FeedEntry, Integer> {

    List<FeedEntry> findByUserIdOrderByPositionAsc(Integer userId);

    @Query("""
SELECT f.postId
FROM FeedEntry f
WHERE f.userId = :userId AND f.position >= :cursor
ORDER BY f.position ASC
""")
    List<Integer> getFeedPostIdsCustom(
            @Param("userId") Integer userId,
            @Param("cursor") long cursor,
            Pageable pageable
    );

    void deleteByUserId(Integer userId);

    List<FeedEntry> findAllByUserId(Integer userId);

    FeedEntry findByPostId(Integer postId);

    FeedEntry findByPostIdAndUserId(Integer postId, Integer userId);
}
