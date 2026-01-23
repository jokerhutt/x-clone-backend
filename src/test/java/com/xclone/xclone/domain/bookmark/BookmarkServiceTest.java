package com.xclone.xclone.domain.bookmark;

import com.xclone.xclone.AbstractServiceTest;
import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.domain.post.PostDTO;
import com.xclone.xclone.helpers.ServiceLayerHelper;
import com.xclone.xclone.utils.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;

import static com.xclone.xclone.helpers.ExceptionAssertHelper.assertIllegalState;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest extends AbstractServiceTest {

    @Autowired
    private BookmarkService bookmarkService;

    private static Post post;
    private static User authUser;

    @BeforeEach
    public void setup() {

        super.setUp();
        authUser = new User();
        authUser.setId(TestConstants.USER_ID);
        when(userRepository.findById(TestConstants.USER_ID)).thenReturn(Optional.of(authUser));

        post = new Post();
        post.setId(TestConstants.TWEET_ID);
        post.setUserId(authUser.getId());
        post.setText(TestConstants.TWEET_TEXT);

    }

    @Test
    public void getBookMarkIds_ShouldReturnAllUserBookmarkedIds() {
        ArrayList<Bookmark> mockBookmarks = ServiceLayerHelper.createMockBookmarkList();

        when(bookmarkRepository.findAllByBookmarkedBy(TestConstants.USER_ID)).thenReturn(mockBookmarks);

        ArrayList<Integer> result = bookmarkService.getAllUserBookmarkedIds(TestConstants.USER_ID);

        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));

    }

    @Test
    public void addNewBookmark_ShouldSaveBookmarkAndReturnDto() {
        Integer userId = authUser.getId();
        Integer postId = post.getId();

        when(bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(userId, postId))
                .thenReturn(false);

        PostDTO mockDto = ServiceLayerHelper.createMockPostDTOWithBookmarks(userId);
        when(postService.findPostDTOById(postId))
                .thenReturn(mockDto);

        PostDTO result = bookmarkService.addNewBookmark(userId, postId);

        assertSame(mockDto, result);
        verify(bookmarkRepository).save(any(Bookmark.class));
        verify(postService).findPostDTOById(postId);
    }

    @Test
    void deleteBookmark_ShouldDeleteAndReturnDto() {
        Integer userId = authUser.getId();
        Integer postId = post.getId();

        Bookmark existing = new Bookmark();
        when(bookmarkRepository.findByBookmarkedByAndBookmarkedPost(userId, postId))
                .thenReturn(Optional.of(existing));

        PostDTO dto = ServiceLayerHelper.createMockPostDTO();
        when(postService.findPostDTOById(postId)).thenReturn(dto);

        PostDTO result = bookmarkService.deleteBookmark(userId, postId);

        assertSame(dto, result);
        verify(bookmarkRepository).delete(existing);
        verify(postService).findPostDTOById(postId);
    }

    @Test
    void addBookmark_ShouldThrow_WhenExists() {

        Integer userId = authUser.getId();
        Integer postId = post.getId();

        when(bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(userId, postId))
                .thenReturn(true);

        assertIllegalState(() -> bookmarkService.addNewBookmark(userId, postId));

    }

    @Test
    void deleteBookmark_ShouldThrow_WhenNotFound() {
        Integer userId = authUser.getId();
        Integer postId = post.getId();

        when(bookmarkRepository.findByBookmarkedByAndBookmarkedPost(userId, postId))
                .thenReturn(Optional.empty());

        assertIllegalState(() -> bookmarkService.deleteBookmark(userId, postId));
    }




}
