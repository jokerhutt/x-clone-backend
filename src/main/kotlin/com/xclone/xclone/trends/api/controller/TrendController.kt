package com.xclone.xclone.trends.api.controller

import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.trends.infra.repository.TrendRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.TRENDS.BASE)
class TrendController(
    private val trendRepository: TrendRepository
) {

    @GetMapping(ApiPaths.TRENDS.GET)
    fun getTrends(): ResponseEntity<Any> {
        val trendEntityList = trendRepository.findAll()
        return ResponseEntity.ok(trendEntityList)
    }

    @GetMapping(ApiPaths.TRENDS.GET_TOP_FIVE)
    fun getTopFiveTrends(): ResponseEntity<Any> {
        return ResponseEntity.ok(trendRepository.findTop5ByTweetVolume())
    }
}