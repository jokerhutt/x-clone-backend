package com.xclone.xclone.domain.bookmark
import com.xclone.xclone.AbstractServiceTest
import com.xclone.xclone.bookmark.app.service.BookmarkService
import com.xclone.xclone.domain.post.Post
import com.xclone.xclone.domain.user.User
import com.xclone.xclone.helpers.ExceptionAssertHelper.assertIllegalState
import com.xclone.xclone.helpers.ServiceLayerHelper
import com.xclone.xclone.utils.TestConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import java.util.Optional

class BookmarkServiceTest : AbstractServiceTest() {

    @Autowired
    private lateinit var bookmarkService: BookmarkService

    companion object {
        private lateinit var post: Post
        private lateinit var authUser: User
    }

    @BeforeEach
    fun setup() {
        super.setUp()

        authUser = User(id = TestConstants.USER_ID, username = TestConstants.USERNAME, email = TestConstants.USER_EMAIL, displayName = TestConstants.DISPLAY_NAME)
        `when`(userRepository.findById(TestConstants.USER_ID)).thenReturn(Optional.of(authUser))

        post = Post(
            id = TestConstants.TWEET_ID,
            userId = authUser.id!!,
            text = TestConstants.TWEET_TEXT
        )
    }

    @Test
    fun getBookMarkIds_ShouldReturnAllUserBookmarkedIds() {
        val mockBookmarks = ServiceLayerHelper.createMockBookmarkList()

        `when`(bookmarkRepository.findAllByBookmarkedBy(TestConstants.USER_ID))
            .thenReturn(mockBookmarks)

        val result = bookmarkService.getAllUserBookmarkedIds(TestConstants.USER_ID)

        assertEquals(2, result.size)
        assertTrue(result.contains(1))
        assertTrue(result.contains(2))
    }

    @Test
    fun addNewBookmark_ShouldSaveBookmarkAndReturnDto() {
        val userId = authUser.id!!
        val postId = post.id!!

        `when`(bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(userId, postId))
            .thenReturn(false)

        val mockDto = ServiceLayerHelper.createMockPostDTOWithBookmarks(userId)
        `when`(postService.findPostDTOById(postId))
            .thenReturn(mockDto)

        val result = bookmarkService.addNewBookmark(userId, postId)

        assertSame(mockDto, result)
        verify(bookmarkRepository).save(any(Bookmark::class.java))
        verify(postService).findPostDTOById(postId)
    }

    @Test
    fun deleteBookmark_ShouldDeleteAndReturnDto() {
        val userId = authUser.id!!
        val postId = post.id!!

        val existing = Bookmark(bookmarkedBy = userId, bookmarkedPost = postId)
        `when`(bookmarkRepository.findByBookmarkedByAndBookmarkedPost(userId, postId))
            .thenReturn(Optional.of(existing))

        val dto = ServiceLayerHelper.createMockPostDTO()
        `when`(postService.findPostDTOById(postId)).thenReturn(dto)

        val result = bookmarkService.deleteBookmark(userId, postId)

        assertSame(dto, result)
        verify(bookmarkRepository).delete(existing)
        verify(postService).findPostDTOById(postId)
    }

    @Test
    fun addBookmark_ShouldThrow_WhenExists() {
        val userId = authUser.id!!
        val postId = post.id!!

        `when`(bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(userId, postId))
            .thenReturn(true)

        assertIllegalState { bookmarkService.addNewBookmark(userId, postId) }
    }

    @Test
    fun deleteBookmark_ShouldThrow_WhenNotFound() {
        val userId = authUser.id!!
        val postId = post.id!!

        `when`(bookmarkRepository.findByBookmarkedByAndBookmarkedPost(userId, postId))
            .thenReturn(Optional.empty())

        assertIllegalState { bookmarkService.deleteBookmark(userId, postId) }
    }
}