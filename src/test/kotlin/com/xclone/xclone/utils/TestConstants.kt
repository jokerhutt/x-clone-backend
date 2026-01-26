package com.xclone.xclone.utils
import java.time.Instant

object TestConstants {

    // ----- Hashtags -----
    const val HASHTAG = "#JetBrains"

    // ----- User 1 -----
    const val USER_ID = 2
    const val USERNAME = "iamthezuck"
    const val DISPLAY_NAME = "MrMarkZuckerBerg"
    const val USER_EMAIL = "zuck@facebook.com"
    const val ABOUT = "Hello world!"
    const val USER_CREATED_AT = "2025-08-01T23:34:32"
    const val PFP_SRC_1 = "https://storage.googleapis.com/xclone-media/deffour.png"
    const val BANNER_SRC = "https://storage.googleapis.com/xclone-media/defaultBanner.jpg"
    const val PINNED_TWEET_ID = 40

    // ----- User 2 -----
    const val USER_ID_2 = 3
    const val USERNAME_2 = "John_Doe"
    const val DISPLAY_NAME_2 = "Hello twitter!"
    const val USER_EMAIL_2 = "john@example.com"
    const val PFP_SRC_2 = "https://storage.googleapis.com/xclone-media/deffive.png"

    // ----- User 3 -----
    const val USER_ID_3 = 4
    const val USERNAME_3 = "Jane_Smith"
    const val DISPLAY_NAME_3 = "Jane Smith"
    const val USER_EMAIL_3 = "jane@example.com"
    const val PFP_SRC_3 = "https://storage.googleapis.com/xclone-media/defsix.png"

    // ----- Auth -----
    const val AUTH_TOKEN = "eyJhbGciOiJIUzI1NiJ9...."

    // ----- Tweet IDs & Text -----
    const val TWEET_ID = 11
    const val TWEET_CREATED_AT = "2024-10-03T20:34:15"
    const val TWEET_TEXT = "#JetBrains https://www.jetbrains.com/ "

    const val TWEET_TEXT_1 = "first test tweet"
    const val TWEET_TEXT_2 = "second test tweet with hashtag $HASHTAG"
    const val TWEET_TEXT_3 = "third test tweet plain text"

    // ----- Poll -----
    const val POLL_ID = 1
    const val POLL_CHOICE_ID = 1
    const val POLL_CHOICE_1 = "test choice 1"
    const val POLL_CHOICE_2 = "test choice 2"

    // ----- Time -----
    val TIME_1: Instant = Instant.parse("2025-01-01T10:00:00Z")
    val TIME_2: Instant = Instant.parse("2025-01-01T10:05:00Z")
    val TIME_3: Instant = Instant.parse("2025-01-01T10:10:00Z")
    val CURSOR_TIME: Instant = TIME_3.plusSeconds(60)
}