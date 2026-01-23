package com.xclone.xclone.domain.poll

import org.springframework.data.jpa.repository.JpaRepository

interface PollChoicesRepository : JpaRepository<PollChoice, Int> {
    fun findAllByPollId(pollId: Int): List<PollChoice>
}