package com.xclone.xclone.domain.post;

import com.xclone.xclone.domain.bookmark.Bookmark;
import com.xclone.xclone.domain.bookmark.infra.repository.BookmarkRepository;
import com.xclone.xclone.domain.post.poll.Poll;
import com.xclone.xclone.domain.post.poll.PollsRepository;
import com.xclone.xclone.storage.CloudStorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PostService {

    private final CloudStorageService cloudStorageService;
    private final UserRepository userRepository;
    private final PollsRepository pollsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NotificationService notificationService;
    private final RetweetRepository retweetRepository;
    private final PostMediaRepository postMediaRepository;
    private final EdgeRanke edgeRank;

    @Autowired
    public PostService(PostRepository postRepository, LikeRepository likeRepository, BookmarkRepository bookmarkRepository, NotificationService notificationService, RetweetRepository retweetRepository, PostMediaRepository postMediaRepository, EdgeRanke edgeRank, CloudStorageService cloudStorageService, UserRepository userRepository, PollsRepository pollsRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.notificationService = notificationService;
        this.retweetRepository = retweetRepository;
        this.postMediaRepository = postMediaRepository;
        this.edgeRank = edgeRank;
        this.cloudStorageService = cloudStorageService;
        this.userRepository = userRepository;
        this.pollsRepository = pollsRepository;
    }

    public PostDTO findPostDTOById(int id) {
        Optional<Post> post = postRepository.findById(id);

        if (post.isPresent()) {
            Post postEntity = post.get();
            return createPostDTO(postEntity);
        } else {
            return null;
        }

    }

    //TODO add some kind of feed refresh intervals?

    public ArrayList<PostDTO> findAllPostDTOByIds( ArrayList<Integer> ids) {
        ArrayList<PostDTO> postDTOs = new ArrayList<>();
        List<Post> posts = postRepository.findAllById(ids);

        for (Post post : posts) {
            postDTOs.add(createPostDTO(post));
        }

        return postDTOs;
    }

    private PostDTO createPostDTO(Post post) {
        ArrayList<Like> likedBy = likeRepository.findAllByLikedPostId(post.getId());
        ArrayList<Integer> likedByIds = new ArrayList<>();

        ArrayList<Bookmark> bookmarks = bookmarkRepository.findAllByBookmarkedPost(post.getId());
        ArrayList<Integer> bookmarkIds = new ArrayList<>();

        ArrayList<Post> replies = postRepository.findAllByParentId(post.getId());
        ArrayList<Integer> repliesIds = new ArrayList<>();

        ArrayList<Retweet> retweets = retweetRepository.findAllByReferenceId(post.getId());
        ArrayList<Integer> retweeters = new ArrayList<>();
        ArrayList<PostMedia> postMedia = postMediaRepository.findAllByPostId(post.getId());

        Integer pollId = null;

        Timestamp pollExpiryTimeStamp = null;

        if (pollsRepository.existsByPostId(post.getId())) {
            Optional<Poll> postPoll = pollsRepository.findByPostId(post.getId());
            if (postPoll.isPresent()) {
                pollId = postPoll.get().getId();
                pollExpiryTimeStamp = postPoll.get().getExpiresAt();
            } else  {
                throw new EntityNotFoundException("Poll with id " + post.getId() + " not found");
            }
        }

        for (Like like : likedBy) {
            likedByIds.add(like.getLikerId());
        }

        for (Bookmark bookmark : bookmarks) {
            bookmarkIds.add(bookmark.getBookmarkedBy());
        }

        for (Post reply: replies) {
            repliesIds.add(reply.getId());
        }

        for (Retweet retweet: retweets) {
            retweeters.add(retweet.getRetweeterId());
        }

        return new PostDTO(post, likedByIds, bookmarkIds, repliesIds, retweeters, postMedia, pollId, pollExpiryTimeStamp);
    }

    public ArrayList<Integer> findAllPostsByUserId(int id) {
        Optional<List<Post>> posts = postRepository.findAllByUserId(id);
        ArrayList<Integer> ids = new ArrayList<>();

        if (posts.isPresent()) {
            for (Post post : posts.get()) {
                if (post.getParentId() == null) {
                    ids.add(post.getId());
                }
            }
        }

        return ids;
    }

    public ArrayList<Integer> findAllPostIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        postRepository.findAll().forEach(post -> {
            if (post.getParentId() == null) {
                ids.add(post.getId());
            }
        });
        return ids;
    }

    public ArrayList<Integer> findAllRepliesByUserId(int id) {
        Optional<List<Post>> posts = postRepository.findAllByUserId(id);
        ArrayList<Integer> ids = new ArrayList<>();
        if (posts.isPresent()) {
            for (Post post : posts.get()) {
                if (post.getParentId() != null) {
                    ids.add(post.getId());
                }
            }
        }

        return ids;
    }

    @Transactional
    public Post createPostEntity(Integer userId, String text, Integer parentId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setText(text);

        if (parentId != null) {
            post.setParentId(parentId);
        }

        System.out.println("Saving post by: " + userId);

        Post newPost = postRepository.save(post);

        return newPost;
    }

    @Transactional
    public User handlePinPost (Integer postId, Integer pinnerId, boolean delete) {

        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new EntityNotFoundException("Post not found");
        Post retrievedPost = post.get();
        System.out.println("Retrieving post by ID: " + retrievedPost.getId());
        System.out.println("Retrieving post by user ID: " + retrievedPost.getUserId());
        if (!Objects.equals(retrievedPost.getUserId(), pinnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not post owner");
        }

        User user = userRepository.findById(pinnerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));



        if (delete) {
            user.setPinnedPostId(null);
        } else {
            user.setPinnedPostId(postId);
        }

        userRepository.save(user);

        return user;

    }

    public void deletePost (Integer postId, Integer deleterId) {
        Optional<Post> post = postRepository.findById(postId);

        if (post.isEmpty()) throw new EntityNotFoundException("Post not found");

        Post postEntity = post.get();
        Integer toDeleteId = postId;

        System.out.println("Deleting post by: " + postEntity.getUserId());
        System.out.println("Deleter: Id" + deleterId);

        if (!deleterId.equals(postEntity.getUserId())) throw new IllegalArgumentException("Failed, not post owner");
        notificationService.deleteAllNonFollowNotificationsByReferenceId(toDeleteId);
        deleteReplies(postEntity);
        postRepository.delete(postEntity);

        System.out.println("Deleted post by: " + postId);

    }

    //Delete all child notifs using DFS
    public void deleteReplies(Post post) {
        List<Post> children = postRepository.findAllByParentId(post.getId());
        for (Post child : children) {
            deleteReplies(child);
            notificationService.deleteAllNonFollowNotificationsByReferenceId(child.getId());
            postRepository.delete(child);
        }
    }

    public void savePostImages(int postId, List<MultipartFile> images) throws IOException {
        for (MultipartFile file : images) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String mimeType = file.getContentType();

            System.out.println("MIME: " + file.getContentType() + ", length: " + file.getContentType().length());

            String url = cloudStorageService.upload(fileName, file.getInputStream(), mimeType);

            PostMedia media = new PostMedia(postId, file.getOriginalFilename(), mimeType, url);

            postMediaRepository.save(media);
        }
    }

}