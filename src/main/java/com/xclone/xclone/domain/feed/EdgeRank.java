package com.xclone.xclone.domain.feed;

import com.xclone.xclone.domain.post.Post;
import com.xclone.xclone.domain.post.PostMediaRepository;
import com.xclone.xclone.domain.post.PostRepository;
import com.xclone.xclone.domain.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xclone.xclone.util.EdgeRankUtils.generateFeedEntriesList;
import static com.xclone.xclone.util.EdgeRankUtils.generatePostRankList;

@Service
public class EdgeRank {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final FeedEntryRepository feedEntryRepository;

    @Autowired
    public EdgeRank (PostRepository postRepository, PostMediaRepository postMediaRepository, LikeRepository likeRepository, @Lazy UserService userService, FeedEntryRepository feedEntryRepository) {
        this.postRepository = postRepository;
        this.postMediaRepository = postMediaRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.feedEntryRepository = feedEntryRepository;
    }

    @Transactional
    public void generateFeed (Integer userId) {
        ArrayList<PostRank> postRanks = buildAndGetNewFeed(userId);
        saveFeed(userId, postRanks);
    }

    public ArrayList<PostRank> buildAndGetNewFeed(Integer userId) {
        UserDTO userDTO = userService.generateUserDTOByUserId(userId);
        List<Post> posts = postRepository.findAllTopLevelPosts();
        ArrayList<PostRank> postRanks = generatePostRankList(posts);
        computeTotalScore(postRanks, userDTO);
        postRanks.sort((a, b) -> Double.compare(b.totalScore, a.totalScore));
        return postRanks;
    }


    @Transactional
    public void saveFeed(Integer userId, ArrayList<PostRank> feed) {
        feedEntryRepository.deleteByUserId(userId);
        ArrayList<FeedEntry> feedEntries = generateFeedEntriesList(userId, feed);
        feedEntryRepository.saveAll(feedEntries);
    }

    private void computeTotalScore (ArrayList<PostRank> postranks, UserDTO feedUser) {
        for (PostRank postRank : postranks) {

            if (!calculateIfOwnRecentPost(postRank, feedUser)) {
                computeAffinity(postRank, feedUser);
                computeWeights(postRank);
            }
            computeTimeDecayValue(postRank);
            postRank.computeTotalScore();
        }
    }

    private void computeTimeDecayValue (PostRank postRank) {
        postRank.timeDecay += computeTimeDecay(postRank.post);
    }

    private void computeAffinity (PostRank postToRank, UserDTO feedUser) {

        List<Integer> postIdsByOther = postRepository.findPostIdsByAuthor(postToRank.post.getUserId());
        Set<Integer> postIdsByOtherSet = new HashSet<>(postIdsByOther);

        postToRank.affinity += computeFollowingAffinity(feedUser, postToRank.post.getUserId());
        postToRank.affinity += computeHasLikedAffinity(feedUser, postIdsByOtherSet);
        postToRank.affinity += computeHasRepliedAffinity(feedUser, postIdsByOtherSet);

    }

    private void computeWeights (PostRank postToRank) {

        postToRank.weight += computeHasMediaAffinity(postToRank);
        postToRank.weight += computeLikeWeights(postToRank);

    }

    private float computeHasMediaAffinity (PostRank postToRank) {
        if (postMediaRepository.findAllByPostId(postToRank.post.getId()).isEmpty()) {
            return 0;
        } else {
            return 0.4f;
        }
    }

    private boolean calculateIfOwnRecentPost (PostRank postRank, UserDTO feedUser) {
        boolean isOwnRecentPost = postRank.post.getUserId().equals(feedUser.id) && ChronoUnit.HOURS.between(postRank.post.getCreatedAt().toLocalDateTime(), LocalDateTime.now()) <= 6;
        if (isOwnRecentPost) {
            postRank.affinity += (2000 + postRank.post.getId());
            postRank.weight += (2000 + postRank.post.getId());
            return true;
        }
        return false;
    }

    private float computeLikeWeights (PostRank postToRank) {
        ArrayList <Like> likes = likeRepository.findAllByLikedPostId(postToRank.post.getId());
        return (float) Math.log(likes.size() + 1);
    }

    private double computeTimeDecay(Post post) {
        LocalDateTime createdAt = post.getCreatedAt().toLocalDateTime();
        long hoursSince = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
        return 1.0 / Math.pow(hoursSince + 1, 4.0);
    }

    private float computeFollowingAffinity (UserDTO feedUser, Integer postOwnerId) {
        if (feedUser.following.contains(postOwnerId)) {
            return 2;
        } else {
            return 1f;
        }
    }

    private float computeHasLikedAffinity (UserDTO feedUser, Set<Integer> postIdsByOtherSet) {
        if (feedUser.likedPosts.stream().anyMatch(postIdsByOtherSet::contains)) {
            return 0.5f;
        } else {
            return 0f;
        }
    }

    private float computeHasRepliedAffinity (UserDTO feedUser, Set<Integer> postIdsByOtherSet) {
        if (feedUser.replies.stream().anyMatch(postIdsByOtherSet::contains)) {
            return 0.5f;
        } else {
            return 0f;
        }
    }




}
