package com.xclone.xclone.domain.retweet;

import com.xclone.xclone.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface RetweetRepository extends JpaRepository<Retweet, Integer> {
    ArrayList<Retweet> findAllByRetweeterId(Integer retweeterId);

    boolean existsByRetweeterIdAndReferenceId(Integer retweeterId, Integer referenceId);

    Retweet findByRetweeterIdAndReferenceId(Integer retweeterId, Integer referenceId);

    ArrayList<Retweet> findAllByReferenceId(Integer referenceId);
}
