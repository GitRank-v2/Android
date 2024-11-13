package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Repository의 정보를 담기 위함 모델
@JsonClass(generateAdapter = true)
data class RepoStats(
    @field:Json(name = "git_repo")
    val git_repo: GitRepo?,
    @field:Json(name = "statistics")
    val statistics: Statistics?,
    @field:Json(name = "languages")
    val languages: Map<String, Int>?,
    @field:Json(name = "languages_stats")
    val languages_stats: RepoContributionStats?,
    @field:Json(name = "profile_urls")
    val profile_urls: List<String>?
)
