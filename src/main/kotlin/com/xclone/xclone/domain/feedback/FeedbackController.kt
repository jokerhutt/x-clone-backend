package com.xclone.xclone.domain.feedback

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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