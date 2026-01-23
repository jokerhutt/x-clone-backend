package com.xclone.xclone.domain.user;

import com.xclone.xclone.constants.BANNED;
import com.xclone.xclone.domain.feed.EdgeRank;
import com.xclone.xclone.domain.post.PostService;
import com.xclone.xclone.storage.CloudStorageService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final BookmarkService bookmarkService;
    private final LikeService likeService;

    private final RetSErvice retweetService;
    private final FollowRepository followRepository;
    private final EdgeRank edgeRank;
    private final CloudStorageService cloudStorageService;

    @Autowired
    public UserService(UserRepository userRepository, PostService postService, BookmarkService bookmarkService, LikeService likeService, RetSErvice retweetService, FollowRepository followRepository, EdgeRank edgeRank, CloudStorageService cloudStorageService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.bookmarkService = bookmarkService;
        this.likeService = likeService;
        this.retweetService = retweetService;
        this.followRepository = followRepository;
        this.edgeRank = edgeRank;
        this.cloudStorageService = cloudStorageService;
    }

    private UserDTO createUserDTO(User user) {
        ArrayList<Integer> userPosts = postService.findAllPostsByUserId(user.getId());
        ArrayList<Integer> userBookmarks = bookmarkService.getAllUserBookmarkedIds(user.getId());
        ArrayList<Integer> userLikes = likeService.getAllUserLikes(user.getId());
        ArrayList<Follow> userFollowing = followRepository.findAllByFollowerId(user.getId());
        ArrayList<Integer> userFollowingIds = new ArrayList<>();
        for (Follow follow : userFollowing) {
            userFollowingIds.add(follow.getFollowedId());
        }
        ArrayList<Integer> userFollowerIds = new ArrayList<>();
        ArrayList<Follow> userFollowers = followRepository.findAllByFollowedId(user.getId());
        for (Follow follow : userFollowers) {
            userFollowerIds.add(follow.getFollowerId());
        }
        ArrayList<Integer> userReplies = postService.findAllRepliesByUserId(user.getId());
        ArrayList<Integer> userRetweets = retweetService.getAllRetweetedPostsByUserID(user.getId());
        return new UserDTO(user, userPosts, userBookmarks, userLikes, userFollowerIds, userFollowingIds, userReplies, userRetweets);
    }

    @Transactional
    public void updateUserProfile(
            int userId,
            MultipartFile profilePicture,
            MultipartFile bannerImage,
            String displayName,
            String username,
            String bio
    ) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        for (String banned : BANNED.WORDS) {
            if (displayName.toLowerCase().contains(banned) || username.toLowerCase().contains(banned) || bio.toLowerCase().contains(banned)) {
                throw new IllegalArgumentException("Please dont");
            }
        }

        if (userRepository.existsUserByUsername(username)) {
            User toCheck =  userRepository.findByUsername(username);
            if (toCheck.getId() != userId) {
                throw new IllegalStateException("Username already exists");
            }
        }

        if (username != null) {
            user.setUsername(username);

        }

        user.setDisplayName(displayName);

        if (user.getBio() == null || user.getBio().equals("")) {
            user.setBio(" ");
        }
        
        user.setBio(bio);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
            String mimeType = profilePicture.getContentType();
            String url = cloudStorageService.upload(fileName, profilePicture.getInputStream(), mimeType);
            user.setProfilePictureUrl(url);
        }

        if (bannerImage != null && !bannerImage.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + bannerImage.getOriginalFilename();
            String mimeType = bannerImage.getContentType();
            String url = cloudStorageService.upload(fileName, bannerImage.getInputStream(), mimeType);
            user.setBannerImageUrl(url);
        }

        userRepository.save(user);
    }



    public UserDTO generateUserDTOByUserId(Integer id) {
        Optional<User> user = this.findById(id);
        if (user.isPresent()) {
            return createUserDTO(user.get());
        } else {
            return null;
        }
    }

    public Map<String, Object> getPaginatedTopUsers(Long cursor, int limit) {
        Timestamp cursorTimestamp = new Timestamp(cursor);
        Pageable pageable = PageRequest.of(0, limit);
        List<Integer> userIds = userRepository.findUserIdsByCreatedAtCustom(cursorTimestamp, pageable);

        Long nextCursor = null;

        if (!userIds.isEmpty() && userIds.size() == limit) {
            Integer lastUserId = userIds.get(userIds.size() - 1);

            Optional<User> lastUser = userRepository.findById(lastUserId);
            if (lastUser.isPresent()) {
                nextCursor = lastUser.get().getCreatedAt().getTime();
            } else {
                throw new IllegalArgumentException("UserId doesn't exist");
            }
        }


        System.out.println(" Users length is: " + userIds.size());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userIds);
        response.put("nextCursor", nextCursor);

        return response;
    }

    public List<Integer> searchUsersByName(String query) {
        List<User> userList = userRepository.searchByUsernameOrDisplayName(query);
        ArrayList<Integer> userIds = new ArrayList<>();
        for (User user : userList) {
            userIds.add(user.getId());
        }
        return userIds;
    }

    public ArrayList<UserDTO> findAllUserDTOByIds( ArrayList<Integer> ids) {
        ArrayList<UserDTO> userDTOs = new ArrayList<>();
        userRepository.findAllById(ids).forEach(user -> {
            userDTOs.add(createUserDTO(user));
        });
        return userDTOs;
    }

    public Optional<User> findById (Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    void generateFeed(Integer userId) {
        edgeRank.generateFeed(userId);
    }

}
