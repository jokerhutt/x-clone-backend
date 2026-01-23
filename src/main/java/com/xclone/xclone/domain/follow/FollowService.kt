package com.xclone.xclone.domain.follow;

import com.xclone.xclone.domain.notification.NotificationService;
import com.xclone.xclone.domain.user.UserDTO;
import com.xclone.xclone.domain.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class FollowService {

    private final NotificationService notificationService;
    private final UserService userService;
    private FollowRepository followRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, NotificationService notificationService, UserService userService) {
        this.followRepository = followRepository;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Transactional
    public UserDTO addNewFollow (Integer followerId, Integer followedId) {

        if (followRepository.existsByFollowedIdAndFollowerId(followedId, followerId)) {
            throw new IllegalStateException("Follow exists");
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowedId(followedId);

        followRepository.save(follow);
        notificationService.createNotificationFromType(followerId, followedId, "follow");

        return userService.generateUserDTOByUserId(followedId);

    }

    @Transactional
    public UserDTO deleteFollow(Integer followerId, Integer followedId) {
        Optional<Follow> toDeleteFollow = followRepository.findByFollowedIdAndFollowerId(followedId, followerId);
        if (toDeleteFollow.isPresent()) {
            followRepository.delete(toDeleteFollow.get());
            notificationService.deleteNotificationFromType(followerId, followedId, "follow");
            return userService.generateUserDTOByUserId(followedId);
        } else {
            throw new IllegalStateException("Follow does not exist");
        }
    }



}
