package com.xclone.xclone.poll.infra.http

import com.xclone.xclone.poll.domain.entity.PollVote
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PollVotesRepository : JpaRepository<PollVote, Int> {

    fun existsByUserIdAndPollId(userId: Int, pollId: Int): Boolean

    fun findByPollIdAndUserId(pollId: Int, userId: Int): Optional<PollVote>
}