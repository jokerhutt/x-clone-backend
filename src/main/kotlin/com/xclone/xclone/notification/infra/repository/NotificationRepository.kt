package com.xclone.xclone.notification.infra.repository

import com.xclone.xclone.notification.domain.entity.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Timestamp

interface NotificationRepository : JpaRepository<Notification, Int> {

    @Modifying
    @Query(
        """
        UPDATE Notification n
        SET n.seen = true
        WHERE n.receiverId = :receiverId
          AND n.seen = false
        """
    )
    fun markAllAsSeen(@Param("receiverId") receiverId: Int): Int

    @Query(
        """
        SELECT n.id
        FROM Notification n
        WHERE n.receiverId = :receiverId
          AND n.seen = false
        """
    )
    fun findUnseenNotificationIds(@Param("receiverId") receiverId: Int): List<Int>

    fun existsBySenderIdAndReceiverIdAndTypeAndReferenceIdAndText(
        senderId: Int,
        receiverId: Int,
        type: String,
        referenceId: Int?,
        text: String?
    ): Boolean

    fun findBySenderIdAndReceiverIdAndTypeAndReferenceIdAndText(
        senderId: Int,
        receiverId: Int,
        type: String,
        referenceId: Int?,
        text: String?
    ): Notification?

    fun findBySenderIdAndReceiverIdAndTypeAndReferenceId(
        senderId: Int,
        receiverId: Int,
        type: String,
        referenceId: Int?
    ): Notification?

    @Query(
        """
        SELECT n.id
        FROM Notification n
        WHERE n.receiverId = :userId
          AND n.createdAt < :cursor
        ORDER BY n.createdAt DESC
        """
    )
    fun findPaginatedNotificationIdsByTime(
        @Param("userId") userId: Int,
        @Param("cursor") cursor: Timestamp,
        pageable: Pageable
    ): List<Int>

    @Modifying
    @Query(
        """
        DELETE FROM Notification n
        WHERE n.referenceId = :id
          AND n.type <> 'follow'
        """
    )
    fun deleteByReferenceIdWhereTypeIsNotFollow(@Param("id") id: Int): Int

    @Query(
        """
        SELECT n
        FROM Notification n
        WHERE n.referenceId = :id
          AND n.type <> 'follow'
        """
    )
    fun findByReferenceIdWhereTypeIsNotFollow(@Param("id") id: Int): List<Notification>
}