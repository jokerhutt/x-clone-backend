package com.xclone.xclone.domain.post

import com.xclone.xclone.domain.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.domain.like.LikeRepository
import com.xclone.xclone.domain.poll.PollsRepository
import com.xclone.xclone.domain.retweet.RetweetRepository
import jakarta.persistence.EntityNotFoundException
import java.sql.Timestamp

class PostMapper(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val retweetRepository: RetweetRepository,
    private val postMediaRepository: PostMediaRepository,
    private val pollsRepository: PollsRepository
) {

    fun toResponse(postId: Int): PostDTO {
        val post = postRepository.findById(postId)
            .orElseThrow { EntityNotFoundException() }

        return toResponse(post)
    }

    fun toResponse(post: Post): PostDTO {
        val postId = post.id ?: throw EntityNotFoundException("Post id is null")

        val likedByIds = ArrayList(
            likeRepository.findAllByLikedPostId(postId).map { it.likerId }
        )

        val bookmarkIds = ArrayList(
            bookmarkRepository.findAllByBookmarkedPost(postId).map { it.bookmarkedBy }
        )

        val repliesIds = ArrayList(
            postRepository.findAllByParentId(postId).mapNotNull { it.id }
        )

        val retweeters = ArrayList(
            retweetRepository.findAllByReferenceId(postId).map { it.retweeterId }
        )

        val postMedia = postMediaRepository.findAllByPostId(postId)

        val postMediaDtos: List<PostMediaDTO> =
            postMediaRepository.findAllByPostId(postId).map { media ->
                PostMediaDTO(
                    id = media.id ?: throw EntityNotFoundException("PostMedia id is null"),
                    postId = media.postId,
                    fileName = media.fileName,
                    mimeType = media.mimeType,
                    url = media.url,
                    storageKey = media.storageKey,
                    createdAt = media.createdAt
                )
            }

        var pollId: Int? = null
        var pollExpiry: Timestamp? = null

        if (pollsRepository.existsByPostId(postId)) {
            val poll = pollsRepository.findByPostId(postId)
                .orElseThrow { EntityNotFoundException("Poll with postId $postId not found") }

            pollId = poll.id
            pollExpiry = poll.expiresAt
        }

        return PostDTO(
            id = post.id,
            userId = post.userId,
            text = post.text,
            createdAt = post.createdAt,
            likedBy = likedByIds,
            bookmarkedBy = bookmarkIds,
            replies = repliesIds,
            parentId = post.parentId,
            retweetedBy = retweeters,
            postMedia = postMediaDtos,
            pollId = pollId,
            pollExpiryTimeStamp = pollExpiry
        )
    }

    fun toResponseList(posts: List<Post>): List<PostDTO> {
        return posts.map { toResponse(it) }
    }

    fun toResponseList(ids: List<Int>): List<PostDTO> {
        return ids.map { toResponse(it) }
    }
}