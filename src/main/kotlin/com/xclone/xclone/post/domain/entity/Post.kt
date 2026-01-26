package com.xclone.xclone.post.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "posts")
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Int,

    @Column(name = "parent_id")
    var parentId: Int? = null,

    @Column(name = "text", length = 180)
    var text: String? = null,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null
)