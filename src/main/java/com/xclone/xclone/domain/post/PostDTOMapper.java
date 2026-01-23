package com.xclone.xclone.domain.post;

import com.xclone.xclone.domain.bookmark.Bookmark;
import com.xclone.xclone.domain.post.poll.Poll;
import com.xclone.xclone.domain.post.poll.PollsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PostDTOMapper {

    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final RetweetRepository retweetRepository;
    private final PostMediaRepository postMediaRepository;
    private final PollsRepository pollsRepository;

    @Autowired
    public PostDTOMapper(
            LikeRepository likeRepository,
            BookmarkRepository bookmarkRepository,
            PostRepository postRepository,
            RetweetRepository retweetRepository,
            PostMediaRepository postMediaRepository,
            PollsRepository pollsRepository
    ) {
        this.likeRepository = likeRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.postRepository = postRepository;
        this.retweetRepository = retweetRepository;
        this.postMediaRepository = postMediaRepository;
        this.pollsRepository = pollsRepository;
    }

    public PostDTO fromPostId(Integer id) {
        Post foundPost =  postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return fromPost(foundPost);
    }

    public PostDTO fromPost(Post post) {
        ArrayList<Integer> likedByIds = likeRepository.findAllByLikedPostId(post.getId())
                .stream().map(Like::getLikerId).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Integer> bookmarkIds = bookmarkRepository.findAllByBookmarkedPost(post.getId())
                .stream().map(Bookmark::getBookmarkedBy).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Integer> repliesIds = postRepository.findAllByParentId(post.getId())
                .stream().map(Post::getId).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Integer> retweeters = retweetRepository.findAllByReferenceId(post.getId())
                .stream().map(Retweet::getRetweeterId).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<PostMedia> postMedia = postMediaRepository.findAllByPostId(post.getId());

        Integer pollId = null;
        Timestamp pollExpiry = null;

        if (pollsRepository.existsByPostId(post.getId())) {
            Optional<Poll> poll = pollsRepository.findByPostId(post.getId());
            if (poll.isPresent()) {
                pollId = poll.get().getId();
                pollExpiry = poll.get().getExpiresAt();
            } else {
                throw new EntityNotFoundException("Poll with id " + post.getId() + " not found");
            }
        }

        return new PostDTO(
                post, likedByIds, bookmarkIds, repliesIds,
                retweeters, postMedia, pollId, pollExpiry
        );
    }
}