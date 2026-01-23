package com.xclone.xclone.domain.follow

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.domain.notification.NotificationService
import com.xclone.xclone.domain.user.UserService
import com.xclone.xclone.domain.user.UserDTO
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val notificationService: NotificationService,
    private val userService: UserService
) {

    @Transactional
    fun addNewFollow (followerId: Int, followedId: Int) : UserDTO {

        if (followRepository.existsByFollowedIdAndFollowerId(followerId, followedId)) {
            throw ApiException(ErrorCode.FOLLOW_EXISTS)
        }

        val newFollow = Follow(
            followerId = followerId,
            followedId = followedId
        )

        followRepository.save(newFollow)
        notificationService.createNotificationFromType(followerId, followedId, "follow")

        return userService.generateUserDTOByUserId(followedId)

    }
    @Transactional
    fun deleteFollow(followerId: Int, followedId: Int) : UserDTO {
        val toDelete = followRepository.findByFollowedIdAndFollowerId(followerId, followedId)
            .orElseThrow { ApiException(ErrorCode.NO_FOLLOW) }
        followRepository.delete(toDelete)
        notificationService.deleteNotificationFromType(followerId, followedId, "follow")
        return userService.generateUserDTOByUserId(followedId)
    }

    }


