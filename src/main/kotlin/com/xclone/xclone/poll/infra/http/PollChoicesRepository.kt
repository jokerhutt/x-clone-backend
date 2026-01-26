package com.xclone.xclone.poll.infra.http

import com.xclone.xclone.poll.domain.entity.PollChoice
import org.springframework.data.jpa.repository.JpaRepository

interface PollChoicesRepository : JpaRepository<PollChoice, Int> {
    fun findAllByPollId(pollId: Int): List<PollChoice>
}