package com.xclone.xclone.user.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.post.app.service.PostService
import com.xclone.xclone.user.infra.http.UserRepository
import com.xclone.xclone.user.app.service.UserService
import com.xclone.xclone.user.api.dto.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.USERS.BASE)
class UserController(
    private val userService: UserService,
    private val postService: PostService,
    private val userRepository: UserRepository
) {

    @GetMapping(ApiPaths.USERS.GET)
    fun getUserById(@RequestParam id: Int): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.generateUserDTOByUserId(id))
    }

    @PostMapping(ApiPaths.USERS.GET_USERS)
    fun getUsers(@RequestBody ids: List<Int>): ResponseEntity<List<UserDTO>> {
        return ResponseEntity.ok(userService.findAllUserDTOByIds(ids))
    }

    @GetMapping(ApiPaths.USERS.TOP_FIVE)
    fun getTopFiveUsers(): ResponseEntity<List<Int>> {
        return ResponseEntity.ok(userRepository.findUserIdsByFollowerCount(99999, 4))
    }

    @GetMapping(ApiPaths.USERS.GET_ADMIN)
    fun getUser(@RequestParam id: Int): ResponseEntity<UserDTO> {
        println("Booyah $id")
        userService.generateFeed(id)
        return ResponseEntity.ok(userService.generateUserDTOByUserId(id))
    }

    @GetMapping(ApiPaths.USERS.SEARCH)
    fun searchUsers(@RequestParam q: String): List<Int> {
        return userService.searchUsersByName(q)
    }

    @GetMapping(ApiPaths.USERS.GET_DISCOVER)
    fun getFeedPage(
        @RequestParam(defaultValue = "0") cursor: Long,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<Any> {
        println("Received request for cursor: $cursor limit $limit")
        return ResponseEntity.ok(userService.getPaginatedTopUsers(cursor, limit))
    }
}