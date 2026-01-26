package com.xclone.xclone.auth.app.service
import com.xclone.xclone.commons.constants.DefaultNameConstants
import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import com.xclone.xclone.auth.app.util.UserIdentityUtils
import com.xclone.xclone.feed.app.service.EdgeRank
import com.xclone.xclone.user.app.service.UserService
import com.xclone.xclone.user.domain.entity.User
import com.xclone.xclone.user.infra.http.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.sql.Timestamp
import java.time.Instant
import java.util.Random
import java.util.UUID
import kotlin.collections.get

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val edgeRank: EdgeRank,
    private val mediaStoragePort: MediaStoragePort,
    private val userService: UserService
) {

    @Transactional
    fun registerTemporaryUser(): User {
        val random = Random()

        val firstName = DefaultNameConstants.DEFAULT_ADJECTIVES[random.nextInt(DefaultNameConstants.DEFAULT_ADJECTIVES.size)]
        val lastName = DefaultNameConstants.DEFAULT_ANIMALS[random.nextInt(DefaultNameConstants.DEFAULT_ANIMALS.size)]
        val defaultPfp = DefaultNameConstants.DEFAULT_PROFILE_URLS[random.nextInt(DefaultNameConstants.DEFAULT_PROFILE_URLS.size)]

        val suffix = random.nextInt(90000) + 10000
        val username = "anonymous$suffix"

        val defaultPfpKey =
            DefaultNameConstants.DEFAULT_PROFILE_KEYS[random.nextInt(DefaultNameConstants.DEFAULT_PROFILE_KEYS.size)]

        val newUser = User(
            username = username,
            email = "$username@gmail.com",
            displayName = "$firstName $lastName",
            profilePictureUrl = defaultPfp,
            bannerImageUrl = "https://storage.googleapis.com/xclone-media/defaultBanner.jpg",
            pfpKey = defaultPfpKey,
            bannerKey = "defaultBanner.jpg",
            verified = false,
            createdAt = Timestamp.from(Instant.now())
        )

        userRepository.save(newUser)
        return newUser
    }


    @Transactional
    fun authenticateGoogleUser(accessToken: String): User {
        val userInfo = UserIdentityUtils.parseGoogleUserInfo(accessToken)

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

        val pfpKey = try {
            val url = URL(pictureUrl)

            url.openStream().use { input ->
                val bytes = input.readBytes()

                val key = "${UUID.randomUUID()}_google_pfp.jpg"

                mediaStoragePort.upload(
                    key = key,
                    inputStream = bytes.inputStream(),
                    contentType = "image/jpeg",
                    contentLength = bytes.size.toLong()
                )

                key
            }
        } catch (e: Exception) {
            DefaultNameConstants.DEFAULT_PROFILE_KEYS[
                Random().nextInt(DefaultNameConstants.DEFAULT_PROFILE_KEYS.size)
            ]
        }

        val suffix = (Math.random() * 90000).toInt() + 10000

        val newUser = User(
            username = UserIdentityUtils.parseGoogleUserName(firstName, lastName, suffix),
            email = email,
            displayName = UserIdentityUtils.parseGoogleDisplayName(firstName, lastName, suffix),
            googleId = googleId,
            profilePictureUrl = pictureUrl,
            bannerImageUrl = "https://storage.googleapis.com/xclone-media/defaultBanner.jpg",
            verified = false,
            pfpKey = pfpKey,
            bannerKey = "defaultBanner.jpg",
            createdAt = Timestamp.from(Instant.now())
        )

        userRepository.save(newUser)
        val dto = userService.generateUserDTOByUserId(newUser.id!!)
        edgeRank.generateFeed(newUser.id!!, dto)

        return newUser
    }



}