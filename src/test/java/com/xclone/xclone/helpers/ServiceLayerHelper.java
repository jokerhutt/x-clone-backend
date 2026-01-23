package com.xclone.xclone.helpers;

import com.xclone.xclone.domain.bookmark.Bookmark;
import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.domain.post.PostDTO;
import com.xclone.xclone.utils.TestConstants;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class ServiceLayerHelper {

    public static User createMockUser() {
        User userEntity = new User();
        userEntity.setId(TestConstants.USER_ID);
        userEntity.setUsername(TestConstants.USERNAME);
        userEntity.setEmail(TestConstants.USER_EMAIL);
        userEntity.setDisplayName(TestConstants.DISPLAY_NAME);
        return userEntity;
    }

    public static Post createMockPost(Integer postId, Integer ownerUserId) {
        Post postEntity = new Post();
        postEntity.setId(postId);
        postEntity.setUserId(ownerUserId);
        postEntity.setText(TestConstants.TWEET_TEXT);
        postEntity.setParentId(null);
        return postEntity;
    }

    public static ArrayList<Bookmark> createMockBookmarkList() {
        User authorUser = createMockUser();

        Post firstPost = createMockPost(1, authorUser.getId());
        Post secondPost = createMockPost(2, authorUser.getId());

        Bookmark firstBookmark = new Bookmark();
        firstBookmark.setId(1);
        firstBookmark.setBookmarkedBy(authorUser.getId());
        firstBookmark.setBookmarkedPost(firstPost.getId());
        firstBookmark.setCreatedAt(Timestamp.from(TestConstants.TIME_1));

        Bookmark secondBookmark = new Bookmark();
        secondBookmark.setId(2);
        secondBookmark.setBookmarkedBy(authorUser.getId());
        secondBookmark.setBookmarkedPost(secondPost.getId());
        secondBookmark.setCreatedAt(Timestamp.from(TestConstants.TIME_2));

        return new ArrayList<>(Arrays.asList(firstBookmark, secondBookmark));
    }

    public static PostDTO createMockPostDTO() {
        Post sourcePost = createMockPost(TestConstants.TWEET_ID, TestConstants.USER_ID);
        return new PostDTO(
                sourcePost,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );
    }

    public static PostDTO createMockPostDTOWithBookmarks(Integer... bookmarkedUserIds) {
        PostDTO postDto = createMockPostDTO();
        postDto.bookmarkedBy.addAll(Arrays.asList(bookmarkedUserIds));
        return postDto;
    }

    public static Bookmark createMockBookmark(Integer bookmarkId, Integer userId, Integer postId, Instant createdAt) {
        Bookmark bookmarkEntity = new Bookmark();
        bookmarkEntity.setId(bookmarkId);
        bookmarkEntity.setBookmarkedBy(userId);
        bookmarkEntity.setBookmarkedPost(postId);
        bookmarkEntity.setCreatedAt(Timestamp.from(createdAt));
        return bookmarkEntity;
    }
}