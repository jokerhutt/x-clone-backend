package com.xclone.xclone.post.app.mapper

import com.xclone.xclone.post.api.dto.response.PostMediaDTO
import com.xclone.xclone.post.domain.entity.PostMedia
import org.springframework.stereotype.Component

@Component
class PostMediaMapper {

    fun toDto(media: PostMedia): PostMediaDTO =
        PostMediaDTO(
            id = media.id!!,
            postId = media.postId,
            fileName = media.fileName,
            mimeType = media.mimeType,
            url = media.url,
            storageKey = media.storageKey,
            createdAt = media.createdAt
        )

    fun toDtos(mediaList: List<PostMedia>): ArrayList<PostMediaDTO> =
        mediaList
            .map(::toDto)
            .toCollection(ArrayList())
}