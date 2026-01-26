package com.xclone.xclone.follow.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.follow.app.service.FollowService
import com.xclone.xclone.follow.api.dto.request.FollowRequest
import com.xclone.xclone.user.api.dto.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.FOLLOWS.BASE)
class FollowController(
    private val followService: FollowService,
) {

    @PostMapping(ApiPaths.FOLLOWS.CREATE)
    fun createFollow(
        @RequestBody followRequest: FollowRequest,
        auth: Authentication
    ): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int
        val followedUserToReturn = followService.addNewFollow(authUserId, followRequest.followedId)
        return ResponseEntity.ok(followedUserToReturn)
    }

    @PostMapping(ApiPaths.FOLLOWS.DELETE)
    fun unfollowUser(
        @RequestBody followRequest: FollowRequest,
        auth: Authentication
    ): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int
        val followedUserToReturn = followService.deleteFollow(authUserId, followRequest.followedId)
        return ResponseEntity.ok(followedUserToReturn)
    }
}