package com.xclone.xclone.notification.app.service

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.notification.api.dto.request.NewNotification
import com.xclone.xclone.notification.api.dto.internal.NotificationDTO
import com.xclone.xclone.notification.domain.entity.Notification
import com.xclone.xclone.notification.infra.repository.NotificationRepository
import com.xclone.xclone.post.infra.http.PostRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class NotificationService(private val notificationRepository: NotificationRepository,
                          private val postRepository: PostRepository
) {

    @Transactional
    fun addNotification(newNotification: NewNotification) : Boolean {
        if (checkExistingNotification(newNotification)) return false
        if (newNotification.senderId == newNotification.receiverId) return false
        val notification = Notification(
            senderId = newNotification.senderId,
            receiverId = newNotification.receiverId,
            seen = false,
            text = newNotification.text,
            referenceId = newNotification.referenceId,
            type = newNotification.type
        )

        return !notificationRepository.existsById(notification.id)
    }

    @Transactional
    fun createReplyNotificationTemplate(senderId: Int, replyPostId: Int): NewNotification {
       val reply = postRepository.findById(replyPostId).orElseThrow{ ApiException(ErrorCode.POST_NOT_FOUND) }
       val parentId = reply.parentId ?: throw ApiException(ErrorCode.NOT_A_REPLY)
       val parentPost = postRepository.findById(parentId).orElseThrow{ ApiException(ErrorCode.POST_NOT_FOUND) }

       return NewNotification(
           senderId = senderId,
           receiverId = parentPost.userId,
           referenceId = reply.id!!,
           text = reply.text!!,
           type = "reply"
       )

    }

    @Transactional
    fun createNotificationFromType(senderId: Int, referenceId: Int, type: String) {
        val toCreate = when (type) {
            "reply" -> createReplyNotificationTemplate(senderId, referenceId)
            "follow" -> createFollowNotificationTemplate(senderId, referenceId, type)
            else -> createNewNotificationTemplateFromPost(senderId, referenceId, type)
        }

        addNotification(toCreate)
    }

    @Transactional
    fun deleteNotificationFromType(senderId: Int, referenceId: Int, type: String) {
        val notification: Notification? = when (type) {
            "follow" -> getFollowNotification(senderId, referenceId, type)
            else -> getNotificationFromSenderAndPost(senderId, referenceId, type)
        }

        notification?.let { notificationRepository.delete(it) }
    }

    fun findAllNotificationDTOsById(ids: List<Int>): List<NotificationDTO> {
        val notifications = notificationRepository.findAllById(ids)
        return notifications.map {
            NotificationDTO(
                id = it.id!!,
                senderId = it.senderId,
                receiverId = it.receiverId,
                referenceId = it.referenceId!!,
                text = it.text!!,
                type = it.type,
                createdAt = it.createdAt,
                seen = it.seen
            )
        }
    }

    @Transactional
    fun getUsersUnseenIdsAndMarkAllAsSeen (receiverId: Int): List<Int> {
        val unseenIds = notificationRepository.findUnseenNotificationIds(receiverId)
        notificationRepository.markAllAsSeen(receiverId)
        return unseenIds
    }

    @Transactional
    fun deleteAllNonFollowNotificationsByReferenceId (referenceId: Int) {
        val toDelete = notificationRepository.findByReferenceIdWhereTypeIsNotFollow(referenceId)
        notificationRepository.deleteAll(toDelete)
    }

    fun createNewNotificationTemplateFromPost(senderId: Int, postId: Int, type: String) : NewNotification {
        val post = postRepository.findById(postId).orElseThrow { ApiException(ErrorCode.POST_NOT_FOUND) }

        return NewNotification(
            senderId = senderId,
            type = type,
            text = post.text!!,
            receiverId = post.userId,
            referenceId = post.id!!
        )
    }

    fun getNotificationFromSenderAndPost(senderId: Int, postId: Int, type: String): Notification {

        val newNotification = createNewNotificationTemplateFromPost(senderId, postId, type)

        return notificationRepository.findBySenderIdAndReceiverIdAndTypeAndReferenceIdAndText(
            newNotification.senderId,
            newNotification.receiverId,
            newNotification.type,
            newNotification.referenceId,
            newNotification.text
        ) ?: throw ApiException(ErrorCode.NOTIFICATION_NOT_FOUND)

    }

    fun createFollowNotificationTemplate (followerId: Int, followedId: Int, type: String) : NewNotification {
       return NewNotification(
           senderId = followerId,
           receiverId = followedId,
           referenceId = followerId,
           type = type,
           text = ""
       )
    }

    fun getFollowNotification(followerId: Int, followingId: Int, type: String) : Notification {
        return notificationRepository.findBySenderIdAndReceiverIdAndTypeAndReferenceId(followerId, followingId, type, followerId)
            ?: throw ApiException(ErrorCode.NO_FOLLOW)
    }

    fun checkExistingNotification (newNotification: NewNotification) : Boolean {
        return notificationRepository.existsBySenderIdAndReceiverIdAndTypeAndReferenceIdAndText(newNotification.senderId, newNotification.receiverId, type = newNotification.type, referenceId = newNotification.referenceId, text = newNotification.text)
    }

}