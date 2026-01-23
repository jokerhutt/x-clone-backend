package com.xclone.xclone.domain.notification

import com.xclone.xclone.commons.ApiPaths
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS.BASE)
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping(ApiPaths.NOTIFICATIONS.GET)
    fun getUsersUnseenNotifications(auth: Authentication): ResponseEntity<Any> {
        val authUserId = auth.principal as Int
        return ResponseEntity.ok(
            notificationService.getUsersUnseenIdsAndMarkAllAsSeen(authUserId)
        )
    }

    @PostMapping(ApiPaths.NOTIFICATIONS.GET_UNSEEN)
    fun getNotifications(@RequestBody ids: List<Int>): ResponseEntity<Any> {
        println("Received request to retrieve notifications")
        return ResponseEntity.ok(notificationService.findAllNotificationDTOsById(ids))
    }
}