package com.xclone.xclone.utils

import com.xclone.xclone.domain.poll.Poll
import com.xclone.xclone.domain.poll.PollsRepository
import com.xclone.xclone.domain.post.Post
import com.xclone.xclone.domain.post.PostMedia
import com.xclone.xclone.domain.post.PostMediaRepository
import com.xclone.xclone.domain.post.PostRepository
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

object PostTestUtils {

    fun createBasicPost(repo: PostRepository, userId: Int): Post {
        val post = Post(
            userId = userId,
            text = "Basic post"
        )
        return repo.save(post)
    }

    fun createPostWithImage(
        postRepo: PostRepository,
        mediaRepo: PostMediaRepository,
        userId: Int
    ): Post {
        val post = createBasicPost(postRepo, userId)

        val media = PostMedia(
            postId = post.id!!,
            fileName = "file.jpg",
            mimeType = "image/jpeg",
            url = "https://example.com/file.jpg"
        )
        mediaRepo.save(media)

        return post
    }

    fun createPostWithPoll(
        postRepo: PostRepository,
        pollRepo: PollsRepository,
        userId: Int
    ): Post {
        val post = createBasicPost(postRepo, userId)

        val poll = Poll(
            postId = post.id!!,
            expiresAt = Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )

        pollRepo.save(poll)
        return post
    }
}