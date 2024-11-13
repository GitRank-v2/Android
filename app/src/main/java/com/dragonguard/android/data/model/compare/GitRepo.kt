package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Repository의 fork수 등을 담기 위한 모델
@JsonClass(generateAdapter = true)
data class GitRepo(
    @field:Json(name = "forks_count")
    val forks_count: Int,
    @field:Json(name = "full_name")
    val full_name: String,
    @field:Json(name = "closed_issues_count")
    val closed_issues_count: Int,
    @field:Json(name = "open_issues_count")
    val open_issues_count: Int,
    @field:Json(name = "stargazers_count")
    val stargazers_count: Int,
    @field:Json(name = "subscribers_count")
    val subscribers_count: Int,
    @field:Json(name = "watchers_count")
    val watchers_count: Int
)