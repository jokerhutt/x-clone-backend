package com.xclone.xclone.domain.like

import com.xclone.xclone.commons.ApiPaths
import com.xclone.xclone.domain.post.PostDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.LIKES.BASE)
class LikeController(
    private val likeService: LikeService
) {

    @PostMapping(ApiPaths.LIKES.CREATE)
    fun createLike(
        @RequestBody newLike: NewLike,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = likeService.addNewLike(authUserId, newLike.likedPostId)
        return ResponseEntity.ok(postToReturn)
    }

    @PostMapping(ApiPaths.LIKES.DELETE)
    fun removeLike(
        @RequestBody newLike: NewLike,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val postToReturn = likeService.deleteLike(authUserId, newLike.likedPostId)
        return ResponseEntity.ok(postToReturn)
    }
}