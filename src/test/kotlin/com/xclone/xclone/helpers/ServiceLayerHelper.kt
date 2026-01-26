package com.xclone.xclone.helpers
import com.xclone.xclone.bookmark.domain.entity.Bookmark
import com.xclone.xclone.post.api.dto.response.PostDTO
import com.xclone.xclone.post.domain.entity.Post
import com.xclone.xclone.user.domain.entity.User
import com.xclone.xclone.utils.TestConstants
import java.sql.Timestamp
import java.time.Instant
import java.util.ArrayList

object ServiceLayerHelper {

    fun createMockUser(): User {
        return User(
            id = TestConstants.USER_ID,
            username = TestConstants.USERNAME,
            email = TestConstants.USER_EMAIL,
            displayName = TestConstants.DISPLAY_NAME,
            pfpKey = "",
            bannerKey = ""
        )
    }

    fun createMockPost(postId: Int?, ownerUserId: Int?): Post {
        return Post(
            id = postId,
            userId = ownerUserId!!,
            text = TestConstants.TWEET_TEXT,
            parentId = null
        )
    }

    fun createMockBookmarkList(): ArrayList<Bookmark> {
        val authorUser = createMockUser()

        val firstPost = createMockPost(1, authorUser.id)
        val secondPost = createMockPost(2, authorUser.id)

        val firstBookmark = Bookmark(
            id = 1,
            bookmarkedBy = authorUser.id!!,
            bookmarkedPost = firstPost.id!!,
            createdAt = Timestamp.from(TestConstants.TIME_1)
        )

        val secondBookmark = Bookmark(
            id = 2,
            bookmarkedBy = authorUser.id!!,
            bookmarkedPost = secondPost.id!!,
            createdAt = Timestamp.from(TestConstants.TIME_2)
        )

        return arrayListOf(firstBookmark, secondBookmark)
    }

    fun createMockPostDTO(): PostDTO {
        val sourcePost = createMockPost(TestConstants.TWEET_ID, TestConstants.USER_ID)

        return PostDTO(
            id = sourcePost.id,
            userId = sourcePost.userId,
            text = sourcePost.text,
            createdAt = sourcePost.createdAt,
            likedBy = emptyList(),
            bookmarkedBy = emptyList(),
            replies = emptyList(),
            parentId = sourcePost.parentId,
            retweetedBy = emptyList(),
            postMedia = emptyList(),
            pollId = null,
            pollExpiryTimeStamp = null
        )
    }

    fun createMockPostDTOWithBookmarks(vararg bookmarkedUserIds: Int): PostDTO {
        return createMockPostDTO().copy(
            bookmarkedBy = bookmarkedUserIds.toList()
        )
    }

    fun createMockBookmark(
        bookmarkId: Int?,
        userId: Int,
        postId: Int,
        createdAt: Instant
    ): Bookmark {
        return Bookmark(
            id = bookmarkId,
            bookmarkedBy = userId,
            bookmarkedPost = postId,
            createdAt = Timestamp.from(createdAt)
        )
    }
}