package com.xclone.xclone.domain.retweet

import com.xclone.xclone.commons.ApiPaths
import com.xclone.xclone.domain.post.api.dto.PostDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.RETWEETS.BASE)
class RetweetController(
    private val retweetService: RetweetService
) {

    @PostMapping(ApiPaths.RETWEETS.CREATE)
    fun newRetweet(
        @RequestBody newRetweet: NewRetweet,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = retweetService.createRetweet(authUserId, newRetweet)
        return ResponseEntity.ok(postToReturn)
    }

    @PostMapping(ApiPaths.RETWEETS.DELETE)
    fun deleteRetweet(
        @RequestBody retweet: NewRetweet,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = retweetService.deleteRetweet(authUserId, retweet)
        return ResponseEntity.ok(postToReturn)
    }
}