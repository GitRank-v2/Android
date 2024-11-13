package com.dragonguard.android.data.model.detail

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileModel(
    @field:Json(name = "commits")
    val commits: Int,
    @field:Json(name = "git_repos")
    val git_repos: List<String>,
    @field:Json(name = "issues")
    val issues: Int,
    @field:Json(name = "organization")
    val organization: String?,
    @field:Json(name = "profile_image")
    val profile_image: String,
    @field:Json(name = "pull_requests")
    val pull_requests: Int,
    @field:Json(name = "rank")
    val rank: Int,
    @field:Json(name = "reviews")
    val reviews: Int
) {
    constructor() : this(0, emptyList(), 0, "", "", 0, 0, 0)
}