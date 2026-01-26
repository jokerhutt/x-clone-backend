package com.xclone.xclone.poll.infra.http

import com.xclone.xclone.poll.domain.entity.Poll
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PollsRepository : JpaRepository<Poll, Int> {

    fun existsByPostId(postId: Int): Boolean

    fun findByPostId(postId: Int): Optional<Poll>
}