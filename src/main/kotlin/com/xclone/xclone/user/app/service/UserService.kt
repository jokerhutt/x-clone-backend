package com.xclone.xclone.user.app.service

import com.xclone.xclone.bookmark.app.service.BookmarkService
import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.feed.app.service.EdgeRank
import com.xclone.xclone.follow.infra.repository.FollowRepository
import com.xclone.xclone.like.app.service.LikeService
import com.xclone.xclone.post.app.service.PostService
import com.xclone.xclone.retweet.app.service.RetweetService
import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import com.xclone.xclone.user.api.dto.UserDTO
import com.xclone.xclone.user.domain.entity.User
import com.xclone.xclone.user.infra.http.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.HashMap
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val postService: PostService,
    private val bookmarkService: BookmarkService,
    private val likeService: LikeService,
    private val followRepository: FollowRepository,
    private val retweetService: RetweetService,
    private val edgeRank: EdgeRank,
    private val mediaStoragePort: MediaStoragePort
) {


    fun findById(id: Int) : User {
        return userRepository.findById(id).orElseThrow { ApiException(ErrorCode.POST_NOT_FOUND) }
    }

    fun findAllUserDTOByIds (ids: List<Int>) : List<UserDTO> {
        return userRepository.findAllById(ids).map { it -> createUserDTO(it) }
    }

    fun createUserDTO (user: User) : UserDTO {
        val userId = user.id!!
        val userPosts = postService.findAllPostsByUserId(userId)
        val userBookmarks = bookmarkService.getAllUserBookmarkedIds(userId)
        val userLikes = likeService.getAllUserLikes(userId)
        val userFollowing = followRepository.findAllByFollowerId(userId)
        val userFollowingIds = userFollowing.map { it -> it.followedId }
        val userFollowers = followRepository.findAllByFollowedId(userId)
        val userFollowerIds = userFollowers.map { it -> it.followerId }
        val userReplies = postService.findAllRepliesByUserId(userId)
        val userRetweets = retweetService.getAllRetweetedPostsByUserId(userId)

        val pfpSignedUrl = user.pfpKey
            ?.takeIf { it.isNotBlank() }
            ?.let { mediaStoragePort.presignedGetUrl(it) }

        val bannerSignedUrl = user.bannerKey
            ?.takeIf { it.isNotBlank() }
            ?.let { mediaStoragePort.presignedGetUrl(it) }

        return UserDTO(
            id = userId,
            username = user.username,
            email = user.email,
            bio = user.bio,
            displayName = user.displayName,
            posts = userPosts,
            bookmarkedPosts = userBookmarks,
            likedPosts = userLikes,
            following = userFollowingIds,
            followers = userFollowerIds,
            createdAt = user.createdAt,
            replies = userReplies,
            retweets = userRetweets,
            profilePictureUrl = pfpSignedUrl,
            bannerImageUrl = bannerSignedUrl,
            pfpKey = user.pfpKey,
            bannerKey = user.bannerKey,
            pinnedPostId = user.pinnedPostId,
            verified = user.verified
        )
    }

    fun updateUserProfile(
        userId: Int,
        profilePicture: MultipartFile?,
        bannerImage: MultipartFile?,
        displayName: String,
        username: String,
        bio: String
    ) {
        val user = userRepository.findById(userId)
            .orElseThrow { ApiException(ErrorCode.USER_NOT_FOUND) }

        val userToCheck = userRepository.findByUsername(username)
        if (userToCheck != null && userToCheck.id != user.id) {
            throw ApiException(ErrorCode.USERNAME_IN_USE)
        }

        user.displayName = displayName
        user.username = username
        user.bio = bio

        if (profilePicture != null && !profilePicture.isEmpty) {
            val original = profilePicture.originalFilename ?: "pfp"
            val key = "${UUID.randomUUID()}_$original"

            mediaStoragePort.upload(
                key = key,
                inputStream = profilePicture.inputStream,
                contentType = profilePicture.contentType,
                contentLength = profilePicture.size
            )

            user.pfpKey = key
        }

        if (bannerImage != null && !bannerImage.isEmpty) {
            val original = bannerImage.originalFilename ?: "banner"
            val key = "${UUID.randomUUID()}_$original"

            mediaStoragePort.upload(
                key = key,
                inputStream = bannerImage.inputStream,
                contentType = bannerImage.contentType,
                contentLength = bannerImage.size
            )

            user.bannerKey = key
        }

        userRepository.save(user)
    }

    fun generateUserDTOByUserId(id: Int): UserDTO {
        val user = findById(id)
        return createUserDTO(user)
    }

    fun getPaginatedTopUsers(cursor: Long, limit: Int): MutableMap<String?, Any?> {
        val cursorTimestamp = Timestamp(cursor)
        val pageable = PageRequest.of(0, limit)
        val userIds = userRepository.findUserIdsByCreatedAtCustom(cursorTimestamp, pageable)

        var nextCursor : Long? = null

        if (!userIds.isEmpty() && userIds.size == limit) {
            val lastUserId = userIds.get(userIds.size - 1)
            val lastUser = userRepository.findById(lastUserId).orElseThrow { ApiException(ErrorCode.USER_NOT_FOUND) }
            nextCursor = lastUser.createdAt?.time
        }

        val response: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        response.put("users", userIds)
        response.put("nextCursor", nextCursor)

        return response
    }

    fun searchUsersByName(query: String) : List<Int> {
        val userList = userRepository.searchByUsernameOrDisplayName(query)
        return userList.map { it -> it.id ?: throw ApiException(ErrorCode.USER_NOT_FOUND) }
    }

    @Transactional
    fun generateFeed(userId: Int) {
        val dto = generateUserDTOByUserId(userId)
        edgeRank.generateFeed(userId, dto)
    }



}