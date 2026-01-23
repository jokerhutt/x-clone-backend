package com.xclone.xclone.domain.user

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.domain.bookmark.app.service.BookmarkService
import com.xclone.xclone.domain.feed.EdgeRank
import com.xclone.xclone.domain.follow.FollowRepository
import com.xclone.xclone.domain.like.LikeService
import com.xclone.xclone.domain.post.PostService
import com.xclone.xclone.domain.retweet.RetweetService
import com.xclone.xclone.storage.CloudStorageService
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val postService: PostService,
    private val bookmarkService: BookmarkService,
    private val likeService: LikeService,
    private val followRepository: FollowRepository,
    private val retweetService: RetweetService,
    private val cloudStorageService: CloudStorageService,
    private val edgeRank: EdgeRank
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
            profilePictureUrl = user.profilePictureUrl,
            bannerImageUrl = user.bannerImageUrl,
            pinnedPostId = user.pinnedPostId,
            verified = user.verified
        )
    }

    fun updateUserProfile (userId: Int, profilePicture: MultipartFile, bannerImage: MultipartFile, displayName: String, username: String, bio: String) {
        val user = userRepository.findById(userId).orElseThrow { ApiException(ErrorCode.USER_NOT_FOUND) }

        val userToCheck = userRepository.findByUsername(user.username)
        if (userToCheck != null && userToCheck.username == user.username) {
            throw ApiException(ErrorCode.USERNAME_IN_USE)
        }
        user.username = displayName
        user.bio = bio

        if (profilePicture != null && !profilePicture.isEmpty) {
            val fileName = "${UUID.randomUUID()}_${profilePicture.originalFilename}"
            val mimeType = profilePicture.contentType
            val url = cloudStorageService.upload(fileName, profilePicture.inputStream, mimeType)
            user.profilePictureUrl = url
        }

        if (bannerImage != null && !bannerImage.isEmpty) {
            val fileName = "${UUID.randomUUID()}_${bannerImage.originalFilename}"
            val mimeType = bannerImage.contentType
            val url = cloudStorageService.upload(fileName, bannerImage.inputStream, mimeType)
            user.bannerImageUrl = url
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
        edgeRank.generateFeed(userId)
    }



}