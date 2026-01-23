package com.xclone.xclone.helpers;

import com.xclone.xclone.domain.bookmark.Bookmark;
import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.utils.TestConstants;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.sql.Timestamp;
import java.time.Instant;

@TestComponent
public class JpaTestFactory {

    @Autowired
    private EntityManager entityManager;

    public User persistUser(String username, String email, String displayName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        entityManager.persist(user);
        return user;
    }

    public Bookmark persistBookmark(int userId, int postId, Instant createdAt) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkedBy(userId);
        bookmark.setBookmarkedPost(postId);
        bookmark.setCreatedAt(Timestamp.from(createdAt));
        entityManager.persist(bookmark);
        return bookmark;
    }

    public Post persistPost(User owner, String text) {
        Post post = new Post();
        post.setUserId(owner.getId());
        post.setText(text);
        entityManager.persist(post);
        return post;
    }

    public User persistDefaultUser() {
        return persistUser(TestConstants.USERNAME, TestConstants.USER_EMAIL, TestConstants.DISPLAY_NAME);
    }

    public User persistSecondUser() {
        return persistUser(TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, TestConstants.DISPLAY_NAME_2);
    }

    public User persistThirdUser() {
        return persistUser(TestConstants.USERNAME_3, TestConstants.USER_EMAIL_3, TestConstants.DISPLAY_NAME_3);
    }

    public Post persistDefaultPost(User owner) {
        return persistPost(owner, TestConstants.TWEET_TEXT_1);
    }
    public Post persistSecondPost(User owner) {
        return persistPost(owner, TestConstants.TWEET_TEXT_2);
    }
    public Post persistThirdPost(User owner) {
        return persistPost(owner, TestConstants.TWEET_TEXT_3);
    }


}