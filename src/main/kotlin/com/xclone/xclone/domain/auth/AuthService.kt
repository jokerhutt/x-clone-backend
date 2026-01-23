package com.xclone.xclone.domain.auth

import com.xclone.xclone.constants.DefaultNameConstants
import com.xclone.xclone.domain.feed.EdgeRank
import com.xclone.xclone.domain.user.User
import com.xclone.xclone.domain.user.UserRepository
import com.xclone.xclone.util.UserIdentityUtils.parseGoogleDisplayName
import com.xclone.xclone.util.UserIdentityUtils.parseGoogleUserInfo
import com.xclone.xclone.util.UserIdentityUtils.parseGoogleUserName
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.util.Random

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val edgeRank: EdgeRank
) {

    @Transactional
    fun registerTemporaryUser(): User {
        val random = Random()

        val firstName = DefaultNameConstants.DEFAULT_ADJECTIVES[random.nextInt(DefaultNameConstants.DEFAULT_ADJECTIVES.size)]
        val lastName = DefaultNameConstants.DEFAULT_ANIMALS[random.nextInt(DefaultNameConstants.DEFAULT_ANIMALS.size)]
        val defaultPfp = DefaultNameConstants.DEFAULT_PROFILE_URLS[random.nextInt(DefaultNameConstants.DEFAULT_PROFILE_URLS.size)]

        val suffix = random.nextInt(90000) + 10000
        val username = "anonymous$suffix"

        val newUser = User(
            username = username,
            email = "$username@gmail.com",
            displayName = "$firstName $lastName",
            profilePictureUrl = defaultPfp,
            bannerImageUrl = "https://storage.googleapis.com/xclone-media/defaultBanner.jpg",
            verified = false,
            createdAt = Timestamp.from(Instant.now())
        )

        userRepository.save(newUser)
        return newUser
    }


    @Transactional
    fun authenticateGoogleUser(accessToken: String): User {
        val userInfo = parseGoogleUserInfo(accessToken)

        if (userInfo == null || !userInfo.containsKey("sub")) {
            throw IllegalStateException("Could not find user")
        }

        val googleId = userInfo["sub"] as String

        val user = userRepository.findByGoogleId(googleId)

        return if (user.isPresent) {
            user.get()
        } else {
            createNewGoogleUser(userInfo)
        }
    }

    @Transactional
    fun createNewGoogleUser(userInfo: Map<*, *>): User {
        val googleId = userInfo["sub"] as String
        val email = userInfo["email"] as String
        val pictureUrl = userInfo["picture"] as String
        val firstName = userInfo["given_name"] as String?
        val lastName = userInfo["family_name"] as String?

        val suffix = (Math.random() * 90000).toInt() + 10000

        val newUser = User(
            username = parseGoogleUserName(firstName, lastName, suffix),
            email = email,
            displayName = parseGoogleDisplayName(firstName, lastName, suffix),
            googleId = googleId,
            profilePictureUrl = pictureUrl,
            bannerImageUrl = "https://storage.googleapis.com/xclone-media/defaultBanner.jpg",
            verified = false,
            createdAt = Timestamp.from(Instant.now())
        )

        userRepository.save(newUser)
        edgeRank.generateFeed(newUser.id!!)

        return newUser
    }



}