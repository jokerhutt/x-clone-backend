package com.xclone.xclone.domain.bookmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.xclone.xclone.domain.bookmark.api.controller.BookmarkController
import com.xclone.xclone.domain.bookmark.app.service.BookmarkService
import com.xclone.xclone.helpers.MvcTestHelper
import com.xclone.xclone.helpers.ServiceLayerHelper
import com.xclone.xclone.security.JwtService
import com.xclone.xclone.security.SecurityConfig
import com.xclone.xclone.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.context.annotation.Import

@WebMvcTest(controllers = [BookmarkController::class])
@Import(SecurityConfig::class)
class BookmarkControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var jwtService: JwtService

    @MockitoBean
    lateinit var bookmarkService: BookmarkService

    data class NewBookmarkBody(
        val bookmarkedPost: Int
    )

    private fun bearer(userId: Int): String {
        val token = "test-token"
        `when`(jwtService.isTokenValid(token)).thenReturn(true)
        `when`(jwtService.extractUserId(token)).thenReturn(userId)
        return "Bearer $token"
    }

    @Test
    fun createBookmark_returns200_andEchoesServiceDto() {
        val authenticatedUserId = TestConstants.USER_ID
        val bookmarkedPostId = TestConstants.TWEET_ID

        val dto = ServiceLayerHelper.createMockPostDTO()
        `when`(bookmarkService.addNewBookmark(eq(authenticatedUserId), eq(bookmarkedPostId)))
            .thenReturn(dto)

        val body = MvcTestHelper.toJson(objectMapper, NewBookmarkBody(bookmarkedPostId))
        val expected = MvcTestHelper.toJson(objectMapper, dto)

        mockMvc.perform(
            post("/api/bookmarks/create")
                .header("Authorization", bearer(authenticatedUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(expected, false))

        verify(bookmarkService).addNewBookmark(authenticatedUserId, bookmarkedPostId)
    }

    @Test
    fun createBookmark_returns409_onIllegalState() {
        val authenticatedUserId = TestConstants.USER_ID
        val bookmarkedPostId = TestConstants.TWEET_ID

        `when`(bookmarkService.addNewBookmark(eq(authenticatedUserId), eq(bookmarkedPostId)))
            .thenThrow(IllegalStateException("Already bookmarked"))

        val body = MvcTestHelper.toJson(objectMapper, NewBookmarkBody(bookmarkedPostId))

        mockMvc.perform(
            post("/api/bookmarks/create")
                .header("Authorization", bearer(authenticatedUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error").value("Already bookmarked"))
    }

    @Test
    fun deleteBookmark_returns200_withServiceDto() {
        val authenticatedUserId = TestConstants.USER_ID
        val bookmarkedPostId = TestConstants.TWEET_ID

        val dto = ServiceLayerHelper.createMockPostDTO()

        `when`(bookmarkService.deleteBookmark(eq(authenticatedUserId), eq(bookmarkedPostId)))
            .thenReturn(dto)

        val body = MvcTestHelper.toJson(objectMapper, NewBookmarkBody(bookmarkedPostId))
        val expected = MvcTestHelper.toJson(objectMapper, dto)

        mockMvc.perform(
            post("/api/bookmarks/delete")
                .header("Authorization", bearer(authenticatedUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(expected, false))

        verify(bookmarkService).deleteBookmark(authenticatedUserId, bookmarkedPostId)
    }
}