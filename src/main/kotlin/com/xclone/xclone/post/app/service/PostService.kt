package com.xclone.xclone.post.app.service

import com.xclone.xclone.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.poll.infra.http.PollsRepository
import com.xclone.xclone.like.infra.repository.LikeRepository
import com.xclone.xclone.notification.app.service.NotificationService
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.app.mapper.PostMapper
import com.xclone.xclone.post.app.mapper.PostMediaMapper
import com.xclone.xclone.post.domain.entity.Post
import com.xclone.xclone.post.domain.entity.PostMedia
import com.xclone.xclone.post.infra.http.PostMediaRepository
import com.xclone.xclone.post.infra.http.PostRepository
import com.xclone.xclone.retweet.infra.http.RetweetRepository
import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import com.xclone.xclone.user.domain.entity.User
import com.xclone.xclone.user.infra.http.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.util.UUID
import kotlin.collections.mapNotNull

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val postMediaRepository: PostMediaRepository,
    private val pollsRepository: PollsRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val retweetRepository: RetweetRepository,
    private val likeRepository: LikeRepository,
    private val mediaStoragePort: MediaStoragePort,
    private val postMediaMapper: PostMediaMapper,
    private val postMapper: PostMapper
) {

    fun getDTO(id: Int): PostDTO? {
        val post = postRepository.findById(id)
        return if (post.isPresent) {
            createPostDTO(post.get())
        } else {
            null
        }
    }

    fun getDTO(ids: List<Int>): List<PostDTO> {
        val posts = postRepository.findAllById(ids)
        return posts.map { post -> createPostDTO(post) }
    }

    fun findAllPostsByUserId(id: Int): ArrayList<Int> {
        val posts = postRepository.findAllByUserId(id)
        val ids = ArrayList<Int>()

        if (posts.isPresent) {
            for (post in posts.get()) {
                if (post.parentId == null) {
                    ids.add(post.id!!)
                }
            }
        }

        return ids
    }

    fun findAllPostIds(): ArrayList<Int> {
        val ids = ArrayList<Int>()

        postRepository.findAll().forEach { post ->
            if (post.parentId == null) {
                ids.add(post.id!!)
            }
        }

        return ids
    }


    private fun createPostDTO(post: Post): PostDTO {
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
        val postMediaDtos = postMediaMapper.toDtos(postMedia)


        val poll = if (pollsRepository.existsByPostId(postId)) {
            pollsRepository.findByPostId(postId)
                .orElseThrow { EntityNotFoundException("Poll with id $postId not found") }
        } else null

        val pollId = poll?.id
        val pollExpiryTimeStamp = poll?.expiresAt

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
            pollExpiryTimeStamp = pollExpiryTimeStamp
        )
    }

    fun findAllRepliesByUserId(id: Int): ArrayList<Int> {
        val posts = postRepository.findAllByUserId(id)
        val ids = ArrayList<Int>()

        if (posts.isPresent) {
            for (post in posts.get()) {
                if (post.parentId != null) {
                    ids.add(post.id!!)
                }
            }
        }

        return ids
    }

    @Transactional
    fun createPostEntity(userId: Int, text: String?, parentId: Int?): Post {
        val post = Post(
            userId = userId,
            text = text,
            parentId = parentId
        )

        return postRepository.save(post)
    }

    @Transactional
    fun handlePinPost(postId: Int, pinnerId: Int, delete: Boolean): User {

        val retrievedPost = postRepository.findById(postId)
            .orElseThrow { EntityNotFoundException("Post not found") }

        if (retrievedPost.userId != pinnerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not post owner")
        }

        val user = userRepository.findById(pinnerId)
            .orElseThrow { EntityNotFoundException("User not found") }

        user.pinnedPostId = if (delete) null else postId
        userRepository.save(user)

        return user
    }

    fun deletePost(postId: Int, deleterId: Int) {
        val postEntity = postRepository.findById(postId)
            .orElseThrow { EntityNotFoundException("Post not found") }

        if (deleterId != postEntity.userId) {
            throw IllegalArgumentException("Failed, not post owner")
        }

        notificationService.deleteAllNonFollowNotificationsByReferenceId(postId)
        deleteReplies(postEntity)
        postRepository.delete(postEntity)
    }

    // Delete all child replies using DFS
    fun deleteReplies(post: Post) {
        val postId = post.id ?: return
        val children: List<Post> = postRepository.findAllByParentId(postId)

        for (child in children) {
            deleteReplies(child)
            val childId = child.id ?: continue
            notificationService.deleteAllNonFollowNotificationsByReferenceId(childId)
            postRepository.delete(child)
        }
    }

    @Throws(IOException::class)
    fun savePostImages(postId: Int, images: List<MultipartFile>) {
        for (file in images) {
            val key = "${UUID.randomUUID()}_${file.originalFilename}"
            val mimeType = file.contentType

            mediaStoragePort.upload(
                key = key,
                inputStream = file.inputStream,
                contentType = mimeType,
                contentLength = file.size
            )

            val media = PostMedia(
                postId = postId,
                fileName = file.originalFilename ?: "",
                mimeType = mimeType ?: "",
                storageKey = key,
                url = key
            )

            postMediaRepository.save(media)
        }
    }





}