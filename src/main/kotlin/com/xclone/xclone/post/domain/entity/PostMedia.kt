package com.xclone.xclone.post.domain.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "post_media")
class PostMedia(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "post_id", nullable = false)
    var postId: Int,

    @Column(name = "file_name", nullable = false)
    var fileName: String,

    @Column(name = "mime_type", nullable = false)
    var mimeType: String,

    @Column(name = "url", nullable = false)
    var url: String,

    @Column(name = "storage_key", nullable = false)
    var storageKey: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis())

)