package com.xclone.xclone.domain.user;

import com.xclone.xclone.domain.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, PostService postService, UserRepository userRepository) {
        this.userService = userService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUserById(@RequestParam Integer id) {
        return ResponseEntity.ok(userService.generateUserDTOByUserId(id));
    }

    @PostMapping("/get-users")
    public ResponseEntity<?> getUsers(@RequestBody ArrayList<Integer> ids) {
        return ResponseEntity.ok(userService.findAllUserDTOByIds(ids));
    }


    @GetMapping("/get-top-five")
    public ResponseEntity<?> getTopFiveUsers() {
        return ResponseEntity.ok(userRepository.findUserIdsByFollowerCount(99999, 4));
    }

    @GetMapping("/getAdminUser")
    public ResponseEntity<UserDTO> getUser(@RequestParam Integer id) {
        System.out.println("Booyah " + id);
        userService.generateFeed(id);
        return ResponseEntity.ok(userService.generateUserDTOByUserId(id));
    }

    @GetMapping("/search")
    public List<Integer> searchUsers(@RequestParam String q) {
        return userService.searchUsersByName(q);
    }

    @GetMapping("/get-discover")
    public ResponseEntity<?> getFeedPage(
            @RequestParam(defaultValue = "0") long cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        System.out.println("Received request for cursor: " + cursor + " limit " + limit);
        return ResponseEntity.ok(userService.getPaginatedTopUsers(cursor, limit));
    }



}