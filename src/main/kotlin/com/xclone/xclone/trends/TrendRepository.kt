package com.xclone.xclone.trends

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TrendRepository : JpaRepository<TrendEntity, Int> {

    @Query(
        value = "SELECT * FROM trends ORDER BY tweet_volume DESC LIMIT 5",
        nativeQuery = true
    )
    fun findTop5ByTweetVolume(): List<TrendEntity>
}