package com.xclone.xclone.bookmark.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "bookmarks")
class Bookmark(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, updatable = false)
        var id: Int? = null,

        @Column(name = "bookmarked_by", nullable = false)
        var bookmarkedBy: Int,

        @Column(name = "bookmarked_post", nullable = false)
        var bookmarkedPost: Int,

        @Column(name = "created_at", updatable = false, insertable = true)
        var createdAt: Timestamp? = null
)