package com.xclone.xclone.auth.app.util

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.URL

object UserIdentityUtils {

    fun parseGoogleDisplayName(firstName: String?, lastName: String?, suffix: Int): String {
        return if (!firstName.isNullOrBlank()) {
            if (!lastName.isNullOrBlank()) {
                "$firstName $lastName"
            } else {
                firstName
            }
        } else {
            "tempAccount$suffix"
        }
    }

    fun parseGoogleUserName(firstName: String?, lastName: String?, suffix: Int): String {
        val baseName = if (!firstName.isNullOrBlank()) firstName.lowercase() else "user"
        return "$baseName$suffix"
    }

    fun parseGoogleUserInfo(accessToken: String): Map<*, *>? {
        val restTemplate = RestTemplate()
        val googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo"

        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)

        val request = HttpEntity<Any>(headers)

        val response = restTemplate.exchange(
            googleUserInfoUrl,
            HttpMethod.GET,
            request,
            Map::class.java
        )

        return response.body
    }

    fun parseGoogleImageToByte(pictureUrl: String): ByteArray {
        try {
            URL(pictureUrl).openStream().use { input ->
                return input.readBytes()
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load Google profile picture", e)
        }
    }

    fun parseDefaultImage(bannerPath: String): ByteArray {
        try {
            UserIdentityUtils::class.java.classLoader.getResourceAsStream(bannerPath).use { input ->
                if (input == null) throw RuntimeException("default banner not found")
                return input.readBytes()
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load default banner image", e)
        }
    }


}