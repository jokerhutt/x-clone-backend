package com.xclone.xclone.poll.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "polls")
class Poll(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "post_id", nullable = false)
    var postId: Int,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null,

    @Column(name = "expires_at")
    var expiresAt: Timestamp? = null
)