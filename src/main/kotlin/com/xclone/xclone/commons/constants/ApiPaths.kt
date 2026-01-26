package com.xclone.xclone.commons.constants

object ApiPaths {

    const val API_PREFIX = "/api"

    object AUTH {
        const val BASE = "$API_PREFIX/auth"
        const val GOOGLE_LOGIN = "/google-login"
        const val UPDATE_PROFILE = "/update-profile"
        const val AUTH_ME = "/me"
        const val DEMO_SIGNUP = "/demo-signup"
    }

    object BOOKMARKS {
        const val BASE = "$API_PREFIX/bookmarks"
        const val CREATE = "/create"
        const val DELETE = "/delete"
    }

    object FOLLOWS {
        const val BASE = "$API_PREFIX/follows"
        const val CREATE = "/follow"
        const val DELETE = "/unfollow"
    }

    object LIKES {
        const val BASE = "$API_PREFIX/likes"
        const val CREATE = "$API_PREFIX/create"
        const val DELETE = "$API_PREFIX/delete"
    }

    object NOTIFICATIONS {
        const val BASE = "$API_PREFIX/notifications"
        const val GET = "/get-notifications"
        const val GET_UNSEEN = "/get-unseen"
    }

    object RETWEETS {
        const val BASE = "$API_PREFIX/retweets"
        const val CREATE = "/create"
        const val DELETE = "/delete"
    }

    object USERS {
        const val BASE = "$API_PREFIX/users"
        const val GET = "/get-user"
        const val GET_USERS = "/get-users"
        const val TOP_FIVE = "/get-top-five"
        const val GET_ADMIN = "/getAdminUser"
        const val SEARCH = "/search"
        const val GET_DISCOVER = "/get-discover"
    }

    object FEED {
        const val BASE = "$API_PREFIX/feed"
        const val GET_PAGE = "/get-feed-page"
    }

    object POLLS {
        const val BASE = "$API_PREFIX/polls"
        const val CHOICES = "/{pollId}/choices"
        const val SUBMIT_VOTE = "/submit-vote"
        const val GET_VOTE = "/{pollId}/getPollVote"
    }

    object POSTS {
        const val BASE = "$API_PREFIX/posts"
    }

    object TRENDS {
        const val BASE = "$API_PREFIX/trends"
        const val GET = "/get"
        const val GET_TOP_FIVE = "/get-top-five"
    }


}