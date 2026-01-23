package com.xclone.xclone.domain.post;

import com.xclone.xclone.domain.post.poll.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;
    private final NotificationService notificationService;
    private final PostRepository postRepository;
    private final UserServicee userService;
    private final PollService pollService;

    @Autowired
    public PostController(PostService postService, NotificationService notificationService, PostRepository postRepository, UserServicee userService, PollService pollService) {
        this.postService = postService;
        this.notificationService = notificationService;
        this.postRepository = postRepository;
        this.userService = userService;
        this.pollService = pollService;
    }

    @PostMapping("/get-posts")
    public ResponseEntity<?> getPost(@RequestBody ArrayList<Integer> ids) {
        System.out.println("Received request to retrieve posts");
        return ResponseEntity.ok(postService.findAllPostDTOByIds(ids));
    }

    @GetMapping("/get-post/{id}")
    public ResponseEntity<?> getSinglePost(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.findPostDTOById(id));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody Integer postId, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();
        System.out.println("Received request to delete post");
        System.out.println(authUserId);
        postService.deletePost(postId, authUserId);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/pin")
    public ResponseEntity<?> pinPost(@RequestParam Integer postId, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();

        System.out.println("JWT says userId = " + authUserId);
        System.out.println("Post ID = " + postId);


        User toReturn = postService.handlePinPost(postId, authUserId, false);
        UserDTO userToReturn = userService.generateUserDTOByUserId(toReturn.getId());
        return ResponseEntity.ok(userToReturn);
    }

    @PostMapping("/unpin")
    public ResponseEntity<?> unpinPost(@RequestParam Integer postId, Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();

        System.out.println("JWT says userId = " + authUserId);
        System.out.println("Post ID = " + postId);

        User toReturn = postService.handlePinPost(postId, authUserId, true);
        UserDTO userToReturn = userService.generateUserDTOByUserId(toReturn.getId());
        return ResponseEntity.ok(userToReturn);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "parentId", required = false) Integer parentId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "pollChoices", required = false) List<String> pollChoices,
            @RequestParam(value = "pollExpiry", required = false) List<String> pollExpiry,
            Authentication auth
    ) throws IOException {
        Integer authUserId = (Integer) auth.getPrincipal();
        Post post = postService.createPostEntity(authUserId, text, parentId);

        if (text.length() < 1 && images == null) {
            throw new IllegalStateException("Text or images are mandatory");
        }

        if (pollChoices != null && parentId == null && pollExpiry != null) {
            pollService.createNewPollForPost(post.getId(), pollChoices, pollExpiry);

        }

        if (images != null && !images.isEmpty()) {
            postService.savePostImages(post.getId(), images);
        }

        if (parentId != null) {
            notificationService.createNotificationFromType(authUserId, post.getId(), "reply");
        }

        return ResponseEntity.ok(postService.findPostDTOById(post.getId()));
    }

}
