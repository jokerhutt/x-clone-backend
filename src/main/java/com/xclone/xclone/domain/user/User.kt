package com.xclone.xclone.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "name", nullable = false, unique = true, length = 64)
    var username: String,

    @Column(name = "password", length = 400)
    var password: String? = null,

    @Column(name = "google_id", unique = true)
    var googleId: String? = null,

    @Column(name = "email", nullable = false, unique = true, length = 45)
    var email: String,

    @Column(name = "display_name", nullable = false, length = 45)
    var displayName: String,

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String? = null,

    @Column(name = "banner_image_url")
    var bannerImageUrl: String? = null,

    @Column(name = "verified")
    var verified: Boolean? = false,

    @Column(name = "bio", length = 180)
    var bio: String? = null,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null,

    @Column(name = "pinned_post_id")
    var pinnedPostId: Int? = null
)