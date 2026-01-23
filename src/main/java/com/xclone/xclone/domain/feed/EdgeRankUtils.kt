package com.xclone.xclone.domain.feed

import com.xclone.xclone.domain.post.Post

object EdgeRankUtils {

    fun generateFeedEntriesList(userId: Int, feed: List<PostRank>): ArrayList<FeedEntry> {
        val feedEntries = ArrayList<FeedEntry>()

        for (i in feed.indices) {
            val pr = feed[i]

            val feedEntry = FeedEntry().apply {
                this.userId = userId
                this.postId = pr.post.id!!
                this.score = pr.totalScore
                this.position = i
            }

            feedEntries.add(feedEntry)
        }

        return feedEntries
    }

    fun generatePostRankList(posts: List<Post>): ArrayList<PostRank> {
        val postRanks = ArrayList<PostRank>()
        for (post in posts) {
            postRanks.add(PostRank(post))
        }
        return postRanks
    }
}