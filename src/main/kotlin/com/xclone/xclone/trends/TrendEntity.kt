package com.xclone.xclone.trends
import jakarta.persistence.*
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