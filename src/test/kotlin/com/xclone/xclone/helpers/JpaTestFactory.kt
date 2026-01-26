package com.xclone.xclone.helpers

import com.xclone.xclone.bookmark.domain.entity.Bookmark
import com.xclone.xclone.post.domain.entity.Post
import com.xclone.xclone.user.domain.entity.User
import com.xclone.xclone.utils.TestConstants
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.sql.Timestamp
import java.time.Instant

@TestComponent
class JpaTestFactory {

    @Autowired
    private lateinit var entityManager: EntityManager

    fun persistUser(username: String, email: String, displayName: String): User {
        val user = User(
            username = username,
            email = email,
            displayName = displayName,
            bannerKey = "",
            pfpKey = ""
        )
        entityManager.persist(user)
        return user
    }

    fun persistBookmark(userId: Int, postId: Int, createdAt: Instant): Bookmark {
        val bookmark = Bookmark(
            bookmarkedBy = userId,
            bookmarkedPost = postId,
            createdAt = Timestamp.from(createdAt)
        )
        entityManager.persist(bookmark)
        return bookmark
    }

    fun persistPost(owner: User, text: String): Post {
        val post = Post(
            userId = owner.id!!,
            text = text
        )
        entityManager.persist(post)
        return post
    }

    fun persistDefaultUser(): User {
        return persistUser(TestConstants.USERNAME, TestConstants.USER_EMAIL, TestConstants.DISPLAY_NAME)
    }

    fun persistSecondUser(): User {
        return persistUser(TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, TestConstants.DISPLAY_NAME_2)
    }

    fun persistThirdUser(): User {
        return persistUser(TestConstants.USERNAME_3, TestConstants.USER_EMAIL_3, TestConstants.DISPLAY_NAME_3)
    }

    fun persistDefaultPost(owner: User): Post {
        return persistPost(owner, TestConstants.TWEET_TEXT_1)
    }

    fun persistSecondPost(owner: User): Post {
        return persistPost(owner, TestConstants.TWEET_TEXT_2)
    }

    fun persistThirdPost(owner: User): Post {
        return persistPost(owner, TestConstants.TWEET_TEXT_3)
    }
}