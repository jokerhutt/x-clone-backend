package com.xclone.xclone.retweet.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.retweet.api.dto.request.RetweetRequest
import com.xclone.xclone.retweet.app.service.RetweetService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.RETWEETS.BASE)
class RetweetController(
    private val retweetService: RetweetService
) {

    @PostMapping(ApiPaths.RETWEETS.CREATE)
    fun newRetweet(
        @RequestBody retweetRequest: RetweetRequest,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = retweetService.createRetweet(authUserId, retweetRequest)
        return ResponseEntity.ok(postToReturn)
    }

    @PostMapping(ApiPaths.RETWEETS.DELETE)
    fun deleteRetweet(
        @RequestBody retweet: RetweetRequest,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = retweetService.deleteRetweet(authUserId, retweet)
        return ResponseEntity.ok(postToReturn)
    }
}