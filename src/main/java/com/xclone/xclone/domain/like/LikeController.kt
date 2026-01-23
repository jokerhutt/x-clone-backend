package com.xclone.xclone.domain.like;
import com.xclone.xclone.domain.bookmark.NewBookmark;
import com.xclone.xclone.domain.post.PostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLike(@RequestBody NewLike newLike, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();

        try {
            PostDTO postToReturn = likeService.addNewLike(authUserId, newLike.getLikedPostId());
            return ResponseEntity.ok(postToReturn);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> removeLike(@RequestBody NewLike newLike, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();
        try {
            PostDTO postToReturn = likeService.deleteLike(authUserId, newLike.getLikedPostId());
            return ResponseEntity.ok(postToReturn);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }


    }



}
