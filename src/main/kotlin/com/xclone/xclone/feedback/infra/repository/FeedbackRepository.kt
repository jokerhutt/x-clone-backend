package com.xclone.xclone.feedback.infra.repository

import com.xclone.xclone.feedback.domain.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Int>