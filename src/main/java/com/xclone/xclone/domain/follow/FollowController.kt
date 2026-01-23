package com.xclone.xclone.domain.follow
import com.xclone.xclone.commons.ApiPaths
import com.xclone.xclone.domain.user.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.FOLLOWS.BASE)
class FollowController(
    private val followService: FollowService,
) {

    @PostMapping(ApiPaths.FOLLOWS.CREATE)
    fun createFollow(
        @RequestBody newFollow: NewFollow,
        auth: Authentication
    ): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int
        val followedUserToReturn = followService.addNewFollow(authUserId, newFollow.followedId)
        return ResponseEntity.ok(followedUserToReturn)
    }

    @PostMapping(ApiPaths.FOLLOWS.DELETE)
    fun unfollowUser(
        @RequestBody newFollow: NewFollow,
        auth: Authentication
    ): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int
        val followedUserToReturn = followService.deleteFollow(authUserId, newFollow.followedId)
        return ResponseEntity.ok(followedUserToReturn)
    }
}