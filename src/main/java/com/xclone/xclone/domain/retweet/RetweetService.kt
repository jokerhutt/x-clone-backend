package com.xclone.xclone.domain.retweet;

import com.xclone.xclone.domain.notification.NotificationService;
import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.domain.post.PostDTO;
import com.xclone.xclone.domain.post.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class RetweetService {

    private final NotificationService notificationService;
    private final RetweetRepository retweetRepository;
    private final PostService postService;

    @Autowired
    public RetweetService(NotificationService notificationService, RetweetRepository retweetRepository, PostService postService) {
        this.notificationService = notificationService;
        this.retweetRepository = retweetRepository;
        this.postService = postService;
    }

    public ArrayList<Integer> getAllRetweetedPostsByUserID(Integer retweeterId) {

        ArrayList<Retweet> retweets =  retweetRepository.findAllByRetweeterId(retweeterId);
        ArrayList<Integer> referenceIds = new ArrayList<>();
        for (Retweet retweet : retweets) {
            referenceIds.add(retweet.getReferenceId());
        }
        return referenceIds;

    }

    public ArrayList<Integer> getAllRetweetersByPostID(Integer postId) {
        ArrayList<Retweet> retweets =  retweetRepository.findAllByReferenceId(postId);
        ArrayList<Integer> retweeters = new ArrayList<>();
        for (Retweet retweet : retweets) {
            retweeters.add(retweet.getRetweeterId());
        }
        return retweeters;
    }



    @Transactional
    public PostDTO createRetweet(Integer retweeterId, NewRetweet newRetweet) {

        if (retweetRepository.existsByRetweeterIdAndReferenceId(retweeterId, newRetweet.referenceId)) {
            throw new IllegalStateException("Retweet exists");

        }

        Retweet retweet = new Retweet();
        retweet.setRetweeterId(retweeterId);
        retweet.setReferenceId(newRetweet.referenceId);
        retweet.setType(newRetweet.type);

        retweetRepository.save(retweet);
        notificationService.createNotificationFromType(retweeterId, newRetweet.referenceId, "repost");

        PostDTO postDTO = postService.findPostDTOById(newRetweet.referenceId);
        if (postDTO == null) throw new IllegalStateException("Post does not exist exists");

        return postDTO;

    }

    @Transactional
    public PostDTO deleteRetweet(Integer retweeterId, NewRetweet newRetweet) {
        Retweet toDelete = retweetRepository.findByRetweeterIdAndReferenceId(retweeterId, newRetweet.referenceId);
        if (toDelete != null) {
            notificationService.deleteNotificationFromType(retweeterId, newRetweet.referenceId, "repost");
            retweetRepository.delete(toDelete);
            PostDTO postDTO = postService.findPostDTOById(newRetweet.referenceId);
            if (postDTO == null) throw new IllegalStateException("Post does not exist exists");

            return postDTO;
        } else {
            throw new IllegalStateException("Retweet not found");
        }

    }




}
