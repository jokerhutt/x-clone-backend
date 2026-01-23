package com.xclone.xclone.domain.feed
import com.xclone.xclone.domain.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.domain.like.LikeRepository
import com.xclone.xclone.domain.notification.NotificationRepository
import org.springframework.data.domain.Pageable;
import com.xclone.xclone.domain.post.PostRepository
import com.xclone.xclone.domain.user.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class FeedService(
    private val postRepository: PostRepository,
    private val edgeRank: EdgeRank,
    private val feedEntryRepository: FeedEntryRepository,
    private val userService: UserService,
    private val likeRepository: LikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getPaginatedPostIds(cursor: Long, limit: Int, userId: Int?, type: String): Map<String, Any?> {
        val pageable: Pageable = PageRequest.of(0, limit)

        val ids: List<Int> = getPaginatedFeed(type, userId ?: 0, cursor, pageable)

        var nextCursor: Long? = null

        if (ids.isEmpty()) {
            nextCursor = null
        } else if (type.equals("For You", ignoreCase = true) && userId != null) {
            val lastPostIdInt = ids.last()
            val feedEntry = feedEntryRepository.findByPostIdAndUserId(lastPostIdInt, userId)
                ?: throw IllegalArgumentException("FeedEntry not found for postId=$lastPostIdInt userId=$userId")

            nextCursor = feedEntry.position?.toLong()
                ?: throw IllegalArgumentException("FeedEntry position is null for postId=$lastPostIdInt userId=$userId")
        } else {
            val lastPostIdInt: Int? = if (ids.size < limit) null else ids.last()

            if (lastPostIdInt != null) {
                nextCursor =
                    if (type.equals("Notifications", ignoreCase = true) && userId != null) {
                        val notification = notificationRepository.findById(lastPostIdInt)
                            .orElseThrow { IllegalArgumentException("NotificationId doesn't exist") }

                        notification.createdAt?.time
                    } else {
                        val post = postRepository.findById(lastPostIdInt)
                            .orElseThrow { IllegalArgumentException("PostId doesn't exist") }

                        post.createdAt.time
                    }
            }
        }

        return hashMapOf(
            "posts" to ids,
            "nextCursor" to nextCursor
        )
    }

    fun getPaginatedFeed(type: String, userId: Int, cursor: Long, pageable: Pageable) : List<Int> {
        val cursorTimestamp = Timestamp(cursor)

        return when (type.lowercase()) {
            "for you" -> getUsersForYouFeed(userId, cursor, pageable)

            "following" -> {
                requireNotNull(userId) { "userId required for following feed" }
                val user = userService.generateUserDTOByUserId(userId)
                postRepository.findPaginatedPostIdsFromFollowedUsersByTime(user.following, cursorTimestamp, pageable)
            }

            "tweets" -> {
                requireNotNull(userId) { "userId required for tweets feed" }
                postRepository.findPostIdsByUserAndReposts(userId, cursorTimestamp, pageable)
            }

            "liked" -> {
                requireNotNull(userId) { "userId required for liked feed" }
                likeRepository.findPaginatedLikedPostIdsByTime(userId, cursorTimestamp, pageable)
            }

            "replies" -> {
                requireNotNull(userId) { "userId required for replies feed" }
                postRepository.findPaginatedReplyIdsByUserIdByTime(userId, cursorTimestamp, pageable)
            }

            "bookmarks" -> {
                requireNotNull(userId) { "userId required for bookmarks feed" }
                bookmarkRepository.findPaginatedBookmarkedPostIdsByTime(userId, cursorTimestamp, pageable)
            }

            "media" -> {
                requireNotNull(userId) { "userId required for media feed" }
                postRepository.findPaginatedPostIdsWithMediaByUserIdByTime(userId, cursorTimestamp, pageable)
            }

            "notifications" -> {
                requireNotNull(userId) { "userId required for notifications feed" }
                notificationRepository.findPaginatedNotificationIdsByTime(userId, cursorTimestamp, pageable)
            }

            else -> throw IllegalArgumentException("Unknown feed type: $type")
        }
    }

    fun getUsersForYouFeed(userId: Int?, cursor: Long, pageable: Pageable): List<Int> {
        if (userId == null) {
            val cursorTimestamp = Timestamp(cursor)
            return postRepository.findNextPaginatedPostIdsByTime(cursorTimestamp, pageable)
        }

        if (cursor == 0L) {
            val postRanks = edgeRank.buildAndGetNewFeed(userId)
            edgeRank.saveFeed(userId, postRanks)
            return feedEntryRepository.getFeedPostIdsCustom(userId, cursor, pageable)
        }

        var ids = feedEntryRepository.getFeedPostIdsCustom(userId, cursor, pageable)

        if (ids.isEmpty()) {
            val postRanks = edgeRank.buildAndGetNewFeed(userId)
            edgeRank.saveFeed(userId, postRanks)
            ids = feedEntryRepository.getFeedPostIdsCustom(userId, cursor, pageable)
        }

        return ids
    }


}