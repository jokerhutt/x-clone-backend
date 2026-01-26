package com.xclone.xclone.retweet.app.service

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.notification.app.service.NotificationService
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.app.service.PostService
import com.xclone.xclone.retweet.infra.http.RetweetRepository
import com.xclone.xclone.retweet.api.dto.request.RetweetRequest
import com.xclone.xclone.retweet.domain.entity.Retweet
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
    fun createRetweet (retweeterId: Int, retweetRequest: RetweetRequest) : PostDTO {
        if (retweetRepository.existsByRetweeterIdAndReferenceId(retweeterId, retweetRequest.referenceId)) {
            throw ApiException(ErrorCode.RETWEET_EXISTS)
        }

        val retweet = Retweet(
            retweeterId = retweeterId,
            referenceId = retweetRequest.referenceId,
            type = retweetRequest.type
        )

        retweetRepository.save(retweet)
        notificationService.createNotificationFromType(retweeterId, retweetRequest.referenceId, "repost")
        val postDTO = postService.findPostDTOById(retweetRequest.referenceId)
            ?: throw ApiException(ErrorCode.POST_NOT_FOUND)

        return postDTO

    }

    @Transactional
    fun deleteRetweet (retweeterId: Int, retweetRequest: RetweetRequest) : PostDTO {
        val toDelete = retweetRepository.findByRetweeterIdAndReferenceId(retweeterId, retweetRequest.referenceId)
            ?: throw ApiException(ErrorCode.RETWEET_NOT_FOUND)
        retweetRepository.delete(toDelete)
        val postDTO = postService.findPostDTOById(retweetRequest.referenceId)
            ?: throw ApiException(ErrorCode.POST_NOT_FOUND)

        return postDTO
    }


}