package com.xclone.xclone.domain.notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/get-unseen")
    public ResponseEntity<?> getUsersUnseenNotifications(Authentication auth) {
        Integer authUserId = (Integer) auth.getPrincipal();
        return ResponseEntity.ok(notificationService.getUsersUnseenIdsAndMarkAllAsSeen(authUserId));
    }

    @PostMapping("/get-notifications")
    public ResponseEntity<?> getNotifications(@RequestBody ArrayList<Integer> ids) {
        System.out.println("Received request to retrieve notifications");
        return ResponseEntity.ok(notificationService.findAllNotificationDTOsById(ids));
    }

}
