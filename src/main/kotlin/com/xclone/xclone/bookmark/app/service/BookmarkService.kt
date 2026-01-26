package com.xclone.xclone.bookmark.app.service

import com.xclone.xclone.bookmark.domain.entity.Bookmark
import com.xclone.xclone.commons.exception.ApiException
import com.xclone.xclone.commons.exception.ErrorCode
import com.xclone.xclone.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.app.service.PostService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class BookmarkService(private val bookmarkRepository: BookmarkRepository, private val postService: PostService) {

    fun getAllUserBookmarkedIds(userId: Int): List<Int> {
        return bookmarkRepository.findAllBookmarkedPostIdsByUserId(userId)
    }

    @Transactional
    fun addNewBookmark (userId: Int, bookmarkedPost: Int) : PostDTO {

        if (bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(userId, bookmarkedPost)) {
            throw ApiException(ErrorCode.BOOKMARK_NOT_FOUND)
        }

        val newBookmark = Bookmark(bookmarkedBy = userId, bookmarkedPost = bookmarkedPost)

        val bookmarkedPost = postService.findPostDTOById(bookmarkedPost)
        if (bookmarkedPost == null) throw ApiException(ErrorCode.POST_NOT_FOUND)

        bookmarkRepository.save(newBookmark)

        return bookmarkedPost
    }

    @Transactional
    fun deleteBookmark (userId: Int, bookmarkedPost: Int) : PostDTO {
        val toDelete = bookmarkRepository.findByBookmarkedByAndBookmarkedPost(userId, bookmarkedPost).orElseThrow { ApiException(
            ErrorCode.BOOKMARK_NOT_FOUND) }
        bookmarkRepository.delete(toDelete)
        val bookmarkedPost = postService.findPostDTOById(bookmarkedPost)
        if (bookmarkedPost == null) throw ApiException(ErrorCode.POST_NOT_FOUND)
        return bookmarkedPost
    }


}