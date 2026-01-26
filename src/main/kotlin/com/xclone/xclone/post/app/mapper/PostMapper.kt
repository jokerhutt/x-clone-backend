package com.xclone.xclone.post.app.mapper

import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.poll.domain.entity.Poll
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.api.dto.response.PostMediaDTO
import com.xclone.xclone.post.domain.entity.Post
import com.xclone.xclone.post.domain.entity.PostMedia
import org.springframework.stereotype.Component

@Component
class PostMapper {

    fun toDto(
        post: Post,
        likedByIds: List<Int>,
        bookmarkedByIds: List<Int>,
        replyIds: List<Int>,
        retweeterIds: List<Int>,
        media: List<PostMedia> = emptyList(),
        poll: Poll? = null,
    ): PostDTO {
        val postId = post.id ?: throw ApiException(ErrorCode.POST_ID_NOT_FOUND)

        return PostDTO(
            id = postId,
            userId = post.userId,
            text = post.text,
            createdAt = post.createdAt,

            likedBy = ArrayList(likedByIds),
            bookmarkedBy = ArrayList(bookmarkedByIds),
            replies = ArrayList(replyIds),
            parentId = post.parentId,
            retweetedBy = ArrayList(retweeterIds),

            postMedia = media
                .map { toMediaDto(it) }
                .toCollection(ArrayList()),

            pollId = poll?.id,
            pollExpiryTimeStamp = poll?.expiresAt
        )
    }

    private fun toMediaDto(media: PostMedia): PostMediaDTO =
        PostMediaDTO(
            id = media.id!!,
            postId = media.postId,
            fileName = media.fileName,
            mimeType = media.mimeType,
            url = media.url,
            storageKey = media.storageKey,
            createdAt = media.createdAt
        )
}