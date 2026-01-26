package com.xclone.xclone.like.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.like.api.dto.request.LikeRequest
import com.xclone.xclone.like.app.service.LikeService
import com.xclone.xclone.post.api.dto.response.PostDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.LIKES.BASE)
class LikeController(
    private val likeService: LikeService
) {

    @PostMapping(ApiPaths.LIKES.CREATE)
    fun createLike(
        @RequestBody likeRequest: LikeRequest,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = likeService.addNewLike(authUserId, likeRequest.likedPostId)
        return ResponseEntity.ok(postToReturn)
    }

    @PostMapping(ApiPaths.LIKES.DELETE)
    fun removeLike(
        @RequestBody likeRequest: LikeRequest,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = likeService.deleteLike(authUserId, likeRequest.likedPostId)
        return ResponseEntity.ok(postToReturn)
    }
}