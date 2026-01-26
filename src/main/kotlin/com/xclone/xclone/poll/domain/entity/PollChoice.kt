package com.xclone.xclone.poll.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "poll_choices")
class PollChoice(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "choice", nullable = false)
    var choice: String,

    @Column(name = "vote_count", nullable = false)
    var voteCount: Int = 0,

    @Column(name = "poll_id", nullable = false)
    var pollId: Int
)