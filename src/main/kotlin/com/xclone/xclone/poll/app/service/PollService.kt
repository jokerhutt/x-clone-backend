package com.xclone.xclone.poll.app.service

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.poll.infra.http.PollChoicesRepository
import com.xclone.xclone.poll.app.util.PollUtils
import com.xclone.xclone.poll.domain.entity.Poll
import com.xclone.xclone.poll.domain.entity.PollChoice
import com.xclone.xclone.poll.domain.entity.PollVote
import com.xclone.xclone.poll.infra.http.PollVotesRepository
import com.xclone.xclone.poll.infra.http.PollsRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PollService(
    private val pollChoicesRepository: PollChoicesRepository,
    private val pollVotesRepository: PollVotesRepository,
    private val pollsRepository: PollsRepository
) {

    fun createNewPollForPost(postId: Int, pollChoices: List<String>, pollExpiry: List<String>) {
        val poll = pollsRepository.save(
            Poll(
                postId = postId,
                expiresAt = PollUtils.parsePollExpiryToTimeStamp(pollExpiry)
            )
        )

        val pollId = poll.id ?: throw IllegalArgumentException("Poll could not be saved")

        for (choice in pollChoices) {
            pollChoicesRepository.save(
                PollChoice(
                    pollId = pollId,
                    choice = choice,
                    voteCount = 0
                )
            )
        }
    }


    @Transactional
    fun submitPollVote(voterId: Int, choiceId: Int, pollId: Int): List<PollChoice> {

        val pollToCheck = pollsRepository.findById(pollId)
        if (pollToCheck.isPresent) {
            val poll = pollToCheck.get()
            if (PollUtils.checkPollExpiry(poll)) {
                throw ApiException(ErrorCode.POLL_EXPIRED)
            }
        }

        val hasVoted = pollVotesRepository.existsByUserIdAndPollId(voterId, pollId)
        if (hasVoted) {
            throw ApiException(ErrorCode.POLL_EXPIRED)
        }

        val vote = PollVote(
            userId = voterId,
            pollChoiceId = choiceId,
            pollId = pollId
        )

        pollVotesRepository.save(vote)

        val selectedChoice = pollChoicesRepository.findById(choiceId)
            .orElseThrow { ApiException(ErrorCode.POLL_CHOICE_NOT_FOUND) }

        selectedChoice.voteCount += 1
        pollChoicesRepository.save(selectedChoice)

        return pollChoicesRepository.findAllByPollId(pollId)
    }


    fun getVotedChoiceId(pollId: Int, userId: Int): Int {
        val pollVote = pollVotesRepository.findByPollIdAndUserId(pollId, userId)
        return if (pollVote.isPresent) pollVote.get().pollChoiceId else -1
    }

    fun getPollChoices(pollId: Int): List<PollChoice> {
        return pollChoicesRepository.findAllByPollId(pollId)
    }


}