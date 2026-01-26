package com.xclone.xclone.feed.api.controller
import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.feed.app.service.FeedService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.FEED.BASE)
class FeedController(
    private val feedService: FeedService
) {

    @GetMapping(ApiPaths.FEED.GET_PAGE)
    fun getFeedPage(
        @RequestParam type: String,
        @RequestParam(defaultValue = "0") cursor: Long,
        @RequestParam(required = false) userId: Int?,
        @RequestParam(defaultValue = "10") limit: Int,
        auth: Authentication?
    ): ResponseEntity<Any> {

        val requiresAuth = when (type.lowercase()) {
            "bookmarks", "notifications", "foryou", "following" -> true
            else -> false
        }

        var finalUserId = userId

        if (requiresAuth) {
            if (auth == null || !auth.isAuthenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login required for feed type: $type")
            }
            finalUserId = auth.principal as Int
        }

        return ResponseEntity.ok(feedService.getPaginatedPostIds(cursor, limit, finalUserId, type))
    }
}