package com.xclone.xclone.commons.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val defaultMessage: String) {

    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "Bookmark not found"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "Like not found"),
    LIKE_EXISTS(HttpStatus.NOT_FOUND, "Like already exists"),
    FOLLOW_EXISTS(HttpStatus.BAD_REQUEST, "Follow already exists"),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification Not Found"),
    NOT_A_REPLY(HttpStatus.BAD_REQUEST, "Post is not a reply"),
    RETWEET_EXISTS(HttpStatus.BAD_REQUEST, "Retweet already exists"),
    RETWEET_NOT_FOUND(HttpStatus.NOT_FOUND, "Retweet not found"),
    NO_FOLLOW(HttpStatus.NOT_FOUND, "Existing follow not found"),

    USERNAME_IN_USE(HttpStatus.BAD_REQUEST, "Username is already in use")

}