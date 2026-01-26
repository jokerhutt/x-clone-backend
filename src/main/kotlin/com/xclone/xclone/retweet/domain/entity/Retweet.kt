package com.xclone.xclone.retweet.domain.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "retweets")
class Retweet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "reference_id", nullable = false)
    var referenceId: Int,

    @Column(name = "retweeter_id", nullable = false)
    var retweeterId: Int,

    @Column(name = "type", nullable = false)
    var type: String
)