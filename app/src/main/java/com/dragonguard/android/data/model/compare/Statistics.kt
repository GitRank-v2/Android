package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//addtion, deletion, commit의 stat을 담기 위한 모델
@JsonClass(generateAdapter = true)
data class Statistics(
    @field:Json(name = "addition_stats")
    val addition_stats: RepoContributionStats,
    @field:Json(name = "commit_stats")
    val commit_stats: RepoContributionStats,
    @field:Json(name = "deletion_stats")
    val deletion_stats: RepoContributionStats
)