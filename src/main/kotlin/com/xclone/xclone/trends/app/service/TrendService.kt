package com.xclone.xclone.trends.app.service

import com.xclone.xclone.trends.infra.repository.TrendRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TrendService(
    private val trendRepository: TrendRepository,
    private val restTemplate: RestTemplate
) {

    @Value("\${x.bearer.token}")
    private lateinit var xBearerToken: String

//    @PostConstruct
//    fun initFetch() {
//        println("[Startup] Fetching trends...")
//        fetchTrends()
//    }
//
//    @Scheduled(cron = "0 0 0 */2 * *")
//    fun fetchTrends() {
//        try {
//            println("[Scheduler] Fetching trends from X API...")
//
//            val headers = HttpHeaders()
//            headers.set("Authorization", "Bearer $xBearerToken")
//            val entity = HttpEntity<String>(headers)
//
//            val response: ResponseEntity<String> = restTemplate.exchange(
//                "https://api.x.com/2/trends/by/woeid/44418",
//                HttpMethod.GET,
//                entity,
//                String::class.java
//            )
//
//            val trends = JSONObject(response.body).getJSONArray("data")
//
//            for (i in 0 until trends.length()) {
//                val trend = trends.getJSONObject(i)
//                val name = trend.getString("trend_name")
//                val volume = trend.optInt("tweet_count", -1)
//
//                val trendEntity = TrendEntity(
//                    name = name,
//                    tweetVolume = volume
//                )
//
//                trendRepository.save(trendEntity)
//            }
//
//            println("[Scheduler] Trends saved successfully.")
//        } catch (e: Exception) {
//            System.err.println("[Scheduler] Failed to fetch or save trends: ${e.message}")
//            e.printStackTrace()
//        }
//    }
}