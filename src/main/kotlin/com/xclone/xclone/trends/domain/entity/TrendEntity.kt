package com.xclone.xclone.trends.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
@Table(name = "trends")
class TrendEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "url")
    var url: String? = null,

    @Column(name = "tweet_volume")
    var tweetVolume: Int? = null,

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    var recordedAt: Timestamp? = null
)