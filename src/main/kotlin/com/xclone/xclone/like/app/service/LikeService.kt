package com.xclone.xclone.like.app.service

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.like.domain.entity.Like
import com.xclone.xclone.like.infra.repository.LikeRepository
import com.xclone.xclone.notification.app.service.NotificationService
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.app.service.PostService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val postService: PostService,
    private val notificationService: NotificationService
) {

    fun getAllUserLikes(likerId: Int): List<Int> {
        return likeRepository.findAllLikedPostIdsByLikerId(likerId)
    }

    @Transactional
    fun addNewLike(likerId: Int, likedPostId: Int) : PostDTO {

        if (likeRepository.existsByLikerIdAndLikedPostId(likerId, likedPostId)) {
            throw ApiException(ErrorCode.LIKE_EXISTS)
        }

        val newLike = Like(
            likerId = likerId,
            likedPostId = likedPostId
        )

        likeRepository.save(newLike)
        notificationService.createNotificationFromType(likerId, likedPostId, "like")

        val postDto = postService.findPostDTOById(likedPostId)
        if (postDto == null) throw ApiException(ErrorCode.POST_NOT_FOUND)
        return postDto
    }

    @Transactional
    fun deleteLike (likerId: Int, likedPostId: Int) : PostDTO {
        val toDelete = likeRepository.findByLikerIdAndLikedPostId(likerId, likedPostId)
            .orElseThrow { ApiException(ErrorCode.LIKE_NOT_FOUND) }
        likeRepository.delete(toDelete)
        notificationService.deleteNotificationFromType(likerId, likedPostId, "like")
        val postDto = postService.findPostDTOById(likedPostId)
        if (postDto == null) throw ApiException(ErrorCode.POST_NOT_FOUND)
        return postDto
    }


}