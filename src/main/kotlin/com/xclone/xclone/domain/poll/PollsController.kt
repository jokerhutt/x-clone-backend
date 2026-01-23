package com.xclone.xclone.domain.poll

import com.xclone.xclone.commons.ApiPaths
import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.POLLS.BASE)
class PollsController(
    private val pollService: PollService
) {

    data class VoteRequest(
        val pollId: Int,
        val choiceId: Int
    )

    @GetMapping(ApiPaths.POLLS.CHOICES)
    fun getChoices(@PathVariable pollId: Int): ResponseEntity<Any> {
        return ResponseEntity.ok(pollService.getPollChoices(pollId))
    }

    @PostMapping(ApiPaths.POLLS.SUBMIT_VOTE)
    fun submitVote(
        @RequestBody voteRequest: VoteRequest,
        auth: Authentication?
    ): ResponseEntity<Any> {
        if (auth == null || !auth.isAuthenticated) {
            throw ApiException(ErrorCode.UNAUTHORIZED)
        }

        val authUserId = auth.principal as Int

        return try {
            val pollChoicesToReturn =
                pollService.submitPollVote(authUserId, voteRequest.choiceId, voteRequest.pollId)

            ResponseEntity.ok(pollChoicesToReturn)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        }
    }

    @GetMapping(ApiPaths.POLLS.GET_VOTE)
    fun getPollVote(
        @PathVariable pollId: Int,
        auth: Authentication?
    ): ResponseEntity<Any> {
        if (auth == null || !auth.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized")
        }

        val authUserId = auth.principal as Int
        val votedChoiceId = pollService.getVotedChoiceId(pollId, authUserId)

        return ResponseEntity.ok(votedChoiceId)
    }
}