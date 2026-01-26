package com.xclone.xclone.bookmark.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.bookmark.api.dto.NewBookmarkRequest
import com.xclone.xclone.bookmark.app.service.BookmarkService
import com.xclone.xclone.post.api.dto.response.PostDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.BOOKMARKS.BASE)
class BookmarkController(
    private val bookmarkService: BookmarkService
) {

    @PostMapping(ApiPaths.BOOKMARKS.CREATE)
    fun createBookmark(
        @RequestBody newBookmark: NewBookmarkRequest,
        auth: Authentication
    ): ResponseEntity<PostDTO> {
        val authUserId = auth.principal as Int
        val bookmarkToReturn = bookmarkService.addNewBookmark(authUserId, newBookmark.bookmarkedPost)
        return ResponseEntity.ok(bookmarkToReturn)
    }

    @PostMapping(ApiPaths.BOOKMARKS.DELETE)
    fun deleteBookmark(
        @RequestBody newBookmark: NewBookmarkRequest,
        auth: Authentication
    ): ResponseEntity<Any> {
        val authUserId = auth.principal as Int
        return ResponseEntity.ok(
            bookmarkService.deleteBookmark(authUserId, newBookmark.bookmarkedPost)
        )
    }
}