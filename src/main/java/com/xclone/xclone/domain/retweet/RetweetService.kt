package com.xclone.xclone.domain.retweet

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.domain.notification.NotificationService
import com.xclone.xclone.domain.post.PostService
import com.xclone.xclone.domain.post.api.dto.PostDTO
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RetweetService(
    private val retweetRepository: RetweetRepository,
    private val notificationService: NotificationService,
    private val postService: PostService
) {


    fun getAllRetweetedPostsByUserId(retweeterId: Int): List<Int> {
        return retweetRepository.findAllReferenceIdsByRetweeterId(retweeterId)
    }

    fun getAllRetweetersByPostId(postId: Int): List<Int> {
        return retweetRepository.findAllRetweeterIdsByPostId(postId)
    }

    @Transactional
    fun createRetweet (retweeterId: Int, newRetweet: NewRetweet) : PostDTO {
        if (retweetRepository.existsByRetweeterIdAndReferenceId(retweeterId, newRetweet.referenceId)) {
            throw ApiException(ErrorCode.RETWEET_EXISTS)
        }

        val retweet = Retweet(
            retweeterId = retweeterId,
            referenceId = newRetweet.referenceId,
            type = newRetweet.type
        )

        retweetRepository.save(retweet)
        notificationService.createNotificationFromType(retweeterId, newRetweet.referenceId, "repost")
        val postDTO = postService.findPostDTOById(newRetweet.referenceId)
            ?: throw ApiException(ErrorCode.POST_NOT_FOUND)

        return postDTO

    }

    @Transactional
    fun deleteRetweet (retweeterId: Int, newRetweet: NewRetweet) : PostDTO {
        val toDelete = retweetRepository.findByRetweeterIdAndReferenceId(retweeterId, newRetweet.referenceId)
            ?: throw ApiException(ErrorCode.RETWEET_NOT_FOUND)
        retweetRepository.delete(toDelete)
        val postDTO = postService.findPostDTOById(newRetweet.referenceId)
            ?: throw ApiException(ErrorCode.POST_NOT_FOUND)

        return postDTO
    }


}