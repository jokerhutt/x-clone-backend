package com.xclone.xclone.domain.notification

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "notifications")
class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "receiver_id", nullable = false)
    var receiverId: Int,

    @Column(name = "sender_id", nullable = false)
    var senderId: Int,

    @Column(name = "type", nullable = false)
    var type: String,

    @Column(name = "reference_id")
    var referenceId: Int? = null,

    @Column(name = "text")
    var text: String? = null,

    @Column(name = "seen", nullable = false)
    var seen: Boolean = false,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null
)