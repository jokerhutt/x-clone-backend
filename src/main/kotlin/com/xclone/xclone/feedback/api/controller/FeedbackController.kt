package com.xclone.xclone.feedback.api.controller

import com.xclone.xclone.feedback.infra.repository.FeedbackRepository
import com.xclone.xclone.feedback.domain.entity.Feedback
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feedback")
class FeedbackController(
    private val feedbackRepository: FeedbackRepository
) {

    @PostMapping("/add-feedback")
    fun addFeedback(@RequestBody newFeedback: Feedback): ResponseEntity<String> {

        val feedback = Feedback(
            userId = newFeedback.userId,
            type = newFeedback.type,
            text = newFeedback.text
        )

        feedbackRepository.save(feedback)

        return ResponseEntity.ok("Feedback received")
    }
}