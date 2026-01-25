package com.xclone.xclone.domain.feed
import com.xclone.xclone.domain.like.Like
import com.xclone.xclone.domain.like.LikeRepository
import com.xclone.xclone.domain.post.Post
import com.xclone.xclone.domain.post.PostMediaRepository
import com.xclone.xclone.domain.post.PostRepository
import com.xclone.xclone.domain.user.UserDTO
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.pow


@Service
class EdgeRank(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val postMediaRepository: PostMediaRepository,
    private val feedEntryRepository: FeedEntryRepository
) {


    @Transactional
    fun generateFeed(userId: Int, userDTO: UserDTO) {
        val postRanks = buildAndGetNewFeed(userId, userDTO)
        saveFeed(userId, postRanks)
    }

    fun buildAndGetNewFeed(userId: Int, userDTO: UserDTO): List<PostRank> {
        val posts = postRepository.findAllTopLevelPosts()
        val postRanks = EdgeRankUtils.generatePostRankList(posts)
        computeTotalScore(postRanks, userDTO)
        return postRanks.sortedByDescending { it.totalScore }
    }


    @Transactional
    fun saveFeed(userId: Int, feed: List<PostRank>) {
        feedEntryRepository.deleteByUserId(userId)
        val feedEntries = EdgeRankUtils.generateFeedEntriesList(userId, feed)
        feedEntryRepository.saveAll(feedEntries)
    }

    private fun computeTotalScore(postRanks: ArrayList<PostRank>, feedUser: UserDTO) {
        for (postRank in postRanks) {
            if (!calculateIfOwnRecentPost(postRank, feedUser)) {
                computeAffinity(postRank, feedUser)
                computeWeights(postRank)
            }
            computeTimeDecayValue(postRank)
            postRank.computeTotalScore()
        }
    }

    private fun computeTimeDecayValue(postRank: PostRank) {
        postRank.timeDecay += computeTimeDecay(postRank.post)
    }

    private fun computeAffinity(postToRank: PostRank, feedUser: UserDTO) {
        val postIdsByOther: List<Int> = postRepository.findPostIdsByAuthor(postToRank.post.userId)
        val postIdsByOtherSet: Set<Int> = postIdsByOther.toHashSet()

        postToRank.affinity += computeFollowingAffinity(feedUser, postToRank.post.userId)
        postToRank.affinity += computeHasLikedAffinity(feedUser, postIdsByOtherSet)
        postToRank.affinity += computeHasRepliedAffinity(feedUser, postIdsByOtherSet)
    }

    private fun computeWeights(postToRank: PostRank) {
        postToRank.weight += computeHasMediaAffinity(postToRank)
        postToRank.weight += computeLikeWeights(postToRank)
    }

    private fun computeHasMediaAffinity(postToRank: PostRank): Float {
        val media = postMediaRepository.findAllByPostId(postToRank.post.id!!)
        return if (media.isEmpty()) 0f else 0.4f
    }

    private fun calculateIfOwnRecentPost(postRank: PostRank, feedUser: UserDTO): Boolean {
        val isOwnRecentPost =
            postRank.post.userId == feedUser.id &&
                    ChronoUnit.HOURS.between(postRank.post.createdAt!!.toLocalDateTime(), LocalDateTime.now()) <= 6

        if (isOwnRecentPost) {
            val boost = 2000 + postRank.post.id!!
            postRank.affinity += boost
            postRank.weight += boost
            return true
        }

        return false
    }

    private fun computeLikeWeights(postToRank: PostRank): Float {
        val likes: List<Like> = likeRepository.findAllByLikedPostId(postToRank.post.id!!)
        return kotlin.math.ln((likes.size + 1).toDouble()).toFloat()
    }

    private fun computeTimeDecay(post: Post): Double {
        val createdAt: LocalDateTime = post.createdAt!!.toLocalDateTime()
        val hoursSince: Long = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now())
        return 1.0 / (hoursSince + 1.0).pow(4.0)
    }

    private fun computeFollowingAffinity(feedUser: UserDTO, postOwnerId: Int): Float {
        return if (feedUser.following.contains(postOwnerId)) 2f else 1f
    }

    private fun computeHasLikedAffinity(feedUser: UserDTO, postIdsByOtherSet: Set<Int>): Float {
        return if (feedUser.likedPosts.any { it in postIdsByOtherSet }) 0.5f else 0f
    }

    private fun computeHasRepliedAffinity(feedUser: UserDTO, postIdsByOtherSet: Set<Int>): Float {
        return if (feedUser.replies.any { it in postIdsByOtherSet }) 0.5f else 0f
    }


}