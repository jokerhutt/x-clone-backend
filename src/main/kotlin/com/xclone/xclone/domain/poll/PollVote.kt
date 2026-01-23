package com.xclone.xclone.domain.poll

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "poll_votes")
class PollVote(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Int? = null,

    @Column(name = "poll_id", nullable = false)
    var pollId: Int,

    @Column(name = "poll_choice_id", nullable = false)
    var pollChoiceId: Int,

    @Column(name = "user_id", nullable = false)
    var userId: Int,

    @Column(name = "created_at", updatable = false, insertable = false)
    var createdAt: Timestamp? = null
)