package com.xclone.xclone.domain.follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    ArrayList<Follow> findAllByFollowerId(Integer followerId);

    boolean existsByFollowedIdAndFollowerId(Integer followedId, Integer followerId);

    Optional<Follow> findByFollowedIdAndFollowerId(Integer followedId, Integer followerId);

    ArrayList<Follow> findAllByFollowedId(Integer followedId);
}
