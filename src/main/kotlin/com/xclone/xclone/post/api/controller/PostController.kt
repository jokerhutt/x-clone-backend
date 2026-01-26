package com.xclone.xclone.post.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.poll.app.service.PollService
import com.xclone.xclone.notification.app.service.NotificationService
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.app.service.PostService
import com.xclone.xclone.user.api.dto.UserDTO
import com.xclone.xclone.user.app.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping(ApiPaths.POSTS.BASE)
class PostController(
    private val postService: PostService,
    private val userService: UserService,
    private val notificationService: NotificationService,
    private val pollService: PollService
) {

    @PostMapping("/get-posts")
    fun getPost(@RequestBody ids: ArrayList<Int>): ResponseEntity<Any> {
        return ResponseEntity.ok(postService.findAllPostDTOByIds(ids))
    }

    @GetMapping("/get-post/{id}")
    fun getSinglePost(@PathVariable id: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(postService.findPostDTOById(id))
    }

    @PostMapping("/delete")
    fun deletePost(@RequestBody postId: Int, auth: Authentication): ResponseEntity<Any> {
        val authUserId = auth.principal as Int
        postService.deletePost(postId, authUserId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/pin")
    fun pinPost(@RequestParam postId: Int, auth: Authentication): ResponseEntity<Any> {
        val authUserId = auth.principal as Int
        val toReturn = postService.handlePinPost(postId, authUserId, false)
        val userToReturn = userService.generateUserDTOByUserId(toReturn.id!!)
        return ResponseEntity.ok(userToReturn)
    }

    @PostMapping("/unpin")
    fun unpinPost(@RequestParam postId: Int, auth: Authentication): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int

        val toReturn = postService.handlePinPost(postId, authUserId, true)
        val userToReturn = userService.generateUserDTOByUserId(toReturn.id!!)

        return ResponseEntity.ok(userToReturn)
    }

    @PostMapping("/create")
    @Throws(IOException::class)
    fun createPost(
        @RequestParam(value = "text", required = false) text: String?,
        @RequestParam(value = "parentId", required = false) parentId: Int?,
        @RequestParam(value = "images", required = false) images: List<MultipartFile>?,
        @RequestParam(value = "pollChoices", required = false) pollChoices: List<String>?,
        @RequestParam(value = "pollExpiry", required = false) pollExpiry: List<String>?,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int

        if ((text == null || text.length < 1) && images.isNullOrEmpty()) {
            throw IllegalStateException("Text or images are mandatory")
        }

        val post = postService.createPostEntity(authUserId, text, parentId)
        val postId = post.id ?: throw IllegalStateException("Post ID was null after creation")

        if (pollChoices != null && parentId == null && pollExpiry != null) {
            pollService.createNewPollForPost(postId, pollChoices, pollExpiry)
        }

        if (!images.isNullOrEmpty()) {
            postService.savePostImages(postId, images)
        }

        if (parentId != null) {
            notificationService.createNotificationFromType(authUserId, postId, "reply")
        }

        return ResponseEntity.ok(postService.findPostDTOById(postId))
    }




}