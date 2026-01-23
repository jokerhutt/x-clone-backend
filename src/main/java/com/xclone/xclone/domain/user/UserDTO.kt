package com.xclone.xclone.domain.user;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;

public class UserDTO {

    public Integer id;
    public String username;
    public String email;
    public String bio;
    public String displayName;
    public ArrayList<Integer> posts;
    public ArrayList<Integer> bookmarkedPosts;
    public ArrayList<Integer> likedPosts;
    public ArrayList<Integer> followers;
    public ArrayList<Integer> following;
    public Timestamp createdAt;
    public ArrayList<Integer> replies;
    public ArrayList<Integer> retweets;
    public String profilePictureUrl;
    public String bannerImageUrl;
    public Integer pinnedPostId;
    public Boolean verified;


    public UserDTO(User user, ArrayList<Integer> posts, ArrayList<Integer> bookmarkedPosts, ArrayList<Integer> likedPosts, ArrayList<Integer> followers, ArrayList<Integer> following, ArrayList<Integer> replies, ArrayList<Integer> retweets) {
        this.id = user.getId();
        this.username = user.getUsername();
        if (user.getVerified()) {
            this.verified = true;
        } else {
            this.verified = false;
        }
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.pinnedPostId = user.getPinnedPostId();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.bannerImageUrl = user.getBannerImageUrl();
        this.displayName = user.getDisplayName();
        this.posts = posts;
        this.bookmarkedPosts = bookmarkedPosts;
        this.likedPosts = likedPosts;
        this.followers = followers;
        this.following = following;
        this.createdAt = user.getCreatedAt();
        this.replies = replies;
        this.retweets = retweets;
    }
}