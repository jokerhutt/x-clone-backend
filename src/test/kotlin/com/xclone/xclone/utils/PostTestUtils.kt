package com.xclone.xclone.utils;

import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.domain.post.PostRepository;
import com.xclone.xclone.domain.post.PostMedia;
import com.xclone.xclone.domain.post.PostMediaRepository;
import com.xclone.xclone.domain.post.poll.Poll;
import com.xclone.xclone.domain.post.poll.PollsRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PostTestUtils {

    public static Post createBasicPost(PostRepository repo, int userId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setText("Basic post");
        return repo.save(post);
    }

    public static Post createPostWithImage(PostRepository postRepo, PostMediaRepository mediaRepo, int userId) {
        Post post = createBasicPost(postRepo, userId);

        PostMedia media = new PostMedia(post.getId(), "file.jpg", "image/jpeg", "https://example.com/file.jpg");
        mediaRepo.save(media);

        return post;
    }

    public static Post createPostWithPoll(PostRepository postRepo, PollsRepository pollRepo, int userId) {
        Post post = createBasicPost(postRepo, userId);

        Poll poll = new Poll();
        poll.setPostId(post.getId());
        poll.setExpiresAt(Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        pollRepo.save(poll);

        return post;
    }
}