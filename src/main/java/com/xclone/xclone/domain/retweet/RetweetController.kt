package com.xclone.xclone.domain.retweet;

import com.xclone.xclone.domain.post.PostDTO;
import com.xclone.xclone.domain.post.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/retweets")
public class RetweetController {

    private final RetweetService retweetService;


    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> newRetweet(@RequestBody NewRetweet newRetweet, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();
        try {
            PostDTO postToReturn = retweetService.createRetweet(authUserId, newRetweet);
            return ResponseEntity.ok(postToReturn);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteRetweet(@RequestBody NewRetweet retweet, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();
        try {
            PostDTO postToReturn = retweetService.deleteRetweet(authUserId, retweet);
            return ResponseEntity.ok(postToReturn);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
