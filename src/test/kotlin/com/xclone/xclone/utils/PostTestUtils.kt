package com.xclone.xclone.utils

import com.xclone.xclone.poll.domain.entity.Poll
import com.xclone.xclone.poll.infra.http.PollsRepository
import com.xclone.xclone.post.domain.entity.Post
import com.xclone.xclone.post.domain.entity.PostMedia
import com.xclone.xclone.post.infra.http.PostMediaRepository
import com.xclone.xclone.post.infra.http.PostRepository
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
            url = "https://example.com/file.jpg",
            storageKey = "file.jpg"
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