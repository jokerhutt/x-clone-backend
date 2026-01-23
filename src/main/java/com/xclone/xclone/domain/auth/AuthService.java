package com.xclone.xclone.domain.auth;

import com.xclone.xclone.domain.feed.EdgeRank;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.xclone.xclone.constants.DEFAULTNAMECONSTANTS.*;
import static com.xclone.xclone.util.UserIdentityUtils.*;

@Service
public class AuthService {

    private final EdgeRank edgeRank;
    UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository, EdgeRank edgeRank) {
        this.userRepository = userRepository;
        this.edgeRank = edgeRank;
    }

    @Transactional
    public User registerTemporaryUser () {

        //TODO make sure this never collides, maybe keep trying until a unique one??
        User newUser = new User();

        Random random = new Random();

        String firstName = DEFAULT_ADJECTIVES[random.nextInt(DEFAULT_ADJECTIVES.length)];
        String lastName = DEFAULT_ANIMALS[random.nextInt(DEFAULT_ANIMALS.length)];
        String defaultPfp = DEFAULT_PROFILE_URLS[random.nextInt(DEFAULT_PROFILE_URLS.length)];


        int suffix = random.nextInt(90000) + 10000;
        String username = "anonymous" + suffix;

        newUser.setDisplayName(firstName + " " + lastName);
        newUser.setUsername(username);
        newUser.setEmail(username + "@gmail.com");
        newUser.setCreatedAt(Timestamp.from(Instant.now()));
        newUser.setProfilePictureUrl(defaultPfp);
        newUser.setBannerImageUrl("https://storage.googleapis.com/xclone-media/defaultBanner.jpg");
        newUser.setVerified(false);
        userRepository.save(newUser);
        return newUser;

    }

    @Transactional
    public User authenticateGoogleUser (String accessToken) {

        Map userInfo = parseGoogleUserInfo(accessToken);

        if (userInfo == null || !userInfo.containsKey("sub")) {
            throw new IllegalStateException("Could not find user");
        }

        Optional<User> user = userRepository.findByGoogleId((String) userInfo.get("sub"));

        if (user.isPresent()) {
            User toReturn = user.get();
            return toReturn;
        } else {
            return createNewGoogleUser(userInfo);
        }
    }

    @Transactional
    public User createNewGoogleUser (Map userInfo) {

        String googleId = (String) userInfo.get("sub");
        String email = (String) userInfo.get("email");
        String pictureUrl = (String) userInfo.get("picture");
        String firstName = (String) userInfo.get("given_name");
        String lastName =  (String) userInfo.get("family_name");

        User newUser = new User();
        newUser.setGoogleId(googleId);

        int suffix = (int)(Math.random() * 90000) + 10000;
        newUser.setUsername(parseGoogleUserName(firstName, lastName, suffix));
        newUser.setDisplayName(parseGoogleDisplayName(firstName, lastName, suffix));
        newUser.setVerified(false);
        newUser.setEmail(email);
        newUser.setCreatedAt(Timestamp.from(Instant.now()));

        newUser.setProfilePictureUrl(pictureUrl);
        newUser.setBannerImageUrl("https://storage.googleapis.com/xclone-media/defaultBanner.jpg");

        userRepository.save(newUser);
        edgeRank.generateFeed(newUser.getId());

        return newUser;

    }

    //TODO make sure this never collides, maybe keep trying until a unique one??






}
