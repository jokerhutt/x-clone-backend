package com.xclone.xclone.domain.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping("/add-feedback")
    public ResponseEntity<?> addFeedback(@RequestBody Feedback newFeedback) {

        Feedback feedback = new Feedback();
        feedback.setUserId(newFeedback.getUserId());
        feedback.setType(newFeedback.getType());
        if (newFeedback.getUserId() != null) {
            feedback.setUserId(newFeedback.getUserId());
        }
        feedback.setText(newFeedback.getText());

        feedbackRepository.save(feedback);

        return ResponseEntity.ok("Feedback received");

    }
}
