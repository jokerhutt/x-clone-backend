package com.xclone.xclone.domain.like

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "likes")
class Like(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "liker_id", nullable = false)
    var likerId: Int,

    @Column(name = "post_id", nullable = false)
    var likedPostId: Int,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null
)