package com.xclone.xclone.domain.like;
import com.xclone.xclone.domain.bookmark.Bookmark;
import com.xclone.xclone.domain.notification.NewNotification;
import com.xclone.xclone.domain.notification.NotificationService;
import com.xclone.xclone.domain.post.PostDTO;
import com.xclone.xclone.domain.post.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class LikeService {

    private final NotificationService notificationService;
    private final PostService postService;
    private LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, NotificationService notificationService, PostService postService) {
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
        this.postService = postService;
    }

    public ArrayList<Integer> getAllUserLikes (Integer likerId) {
        ArrayList<Integer> likeIds = new ArrayList<>();
        ArrayList<Like> likes =  likeRepository.findAllByLikerId(likerId);
        for (Like like : likes) {
            likeIds.add(like.getLikedPostId());
        }
        return likeIds;
    }

    @Transactional
    public PostDTO addNewLike(Integer likerId, Integer likedPostId) {

        if (likeRepository.existsByLikerIdAndLikedPostId(likerId, likedPostId)) {
            throw new IllegalStateException("Like already exists");
        }

        Like like = new Like();
        like.setLikedPostId(likedPostId);
        like.setLikerId(likerId);
        likeRepository.save(like);
        notificationService.createNotificationFromType(likerId, likedPostId, "like");

        PostDTO postDTO = postService.findPostDTOById(likedPostId);
        if (postDTO == null) throw new IllegalStateException("Post does not exist exists");

        return postDTO;

    }

    @Transactional
    public PostDTO deleteLike(Integer likerId, Integer likedPostId) {

        Optional<Like> toDelete = likeRepository.findByLikerIdAndLikedPostId(likerId, likedPostId);
        if (!toDelete.isPresent()) throw new IllegalStateException("Like to delete does not exist");

        likeRepository.delete(toDelete.get());
        notificationService.deleteNotificationFromType(likerId, likedPostId, "like");

        PostDTO postDTO = postService.findPostDTOById(likedPostId);
        if (postDTO == null) throw new IllegalStateException("Post does not exist exists");

        return postDTO;


    }

}
