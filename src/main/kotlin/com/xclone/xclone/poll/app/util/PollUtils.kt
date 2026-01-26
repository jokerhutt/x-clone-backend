package com.xclone.xclone.poll.app.util

import com.xclone.xclone.poll.domain.entity.Poll
import java.sql.Timestamp
import java.time.LocalDateTime

object PollUtils {

    fun checkPollExpiry(poll: Poll): Boolean {
        val expiresAt = poll.expiresAt ?: return true
        return expiresAt.before(Timestamp(System.currentTimeMillis()))
    }

    fun parsePollExpiryToTimeStamp(pollExpiry: List<String>): Timestamp {
        val days = pollExpiry[0].toInt()
        val hours = pollExpiry[1].toInt()
        val minutes = pollExpiry[2].toInt()

        val now = LocalDateTime.now()
        val expiration = now.plusDays(days.toLong()).plusHours(hours.toLong()).plusMinutes(minutes.toLong())
        return Timestamp.valueOf(expiration)
    }
}