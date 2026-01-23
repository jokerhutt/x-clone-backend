package com.xclone.xclone.domain.feed;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    FeedService feedService;

    public FeedController (FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/get-feed-page")
    public ResponseEntity<?> getFeedPage(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") long cursor,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "10") int limit,
            Authentication auth
    ) {
        boolean requiresAuth = switch (type.toLowerCase()) {
            case "bookmarks", "notifications", "foryou", "following" -> true;
            default -> false;
        };

        if (requiresAuth) {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Login required for feed type: " + type);
            }
            userId = (Integer) auth.getPrincipal();
        }

        System.out.println("Received request for type " + type + " cursor " + cursor + " limit " + limit);
        return ResponseEntity.ok(feedService.getPaginatedPostIds(cursor, limit, userId, type));
    }

}
