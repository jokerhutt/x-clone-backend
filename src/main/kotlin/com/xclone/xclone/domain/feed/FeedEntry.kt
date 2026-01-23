package com.xclone.xclone.domain.feed

import jakarta.persistence.*

@Entity
@Table(name = "feed_entry")
class FeedEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "user_id", nullable = false)
    var userId: Int = 0

    @Column(name = "post_id", nullable = false)
    var postId: Int = 0

    @Column(name = "score")
    var score: Double? = null

    @Column(name = "position")
    var position: Int? = null
}