package com.xclone.xclone.follow.infra.repository

import com.xclone.xclone.follow.domain.entity.Follow
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface FollowRepository : JpaRepository<Follow, Int> {

    fun findAllByFollowerId(followerId: Int): List<Follow>

    fun existsByFollowedIdAndFollowerId(followedId: Int, followerId: Int): Boolean

    fun findByFollowedIdAndFollowerId(followedId: Int, followerId: Int): Optional<Follow>

    fun findAllByFollowedId(followedId: Int): List<Follow>
}