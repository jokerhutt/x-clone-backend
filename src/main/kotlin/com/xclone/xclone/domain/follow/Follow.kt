package com.xclone.xclone.domain.follow

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "follows")
class Follow(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "followed_id", nullable = false)
    var followedId: Int,

    @Column(name = "follower_id", nullable = false)
    var followerId: Int
)