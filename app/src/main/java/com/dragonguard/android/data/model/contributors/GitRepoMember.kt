package com.dragonguard.android.data.model.contributors

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitRepoMember(
    @field:Json(name = "additions")
    val additions: Int?,
    @field:Json(name = "commits")
    val commits: Int?,
    @field:Json(name = "deletions")
    val deletions: Int?,
    @field:Json(name = "github_id")
    val github_id: String?,
    @field:Json(name = "is_service_member")
    val is_service_member: Boolean?,
    @field:Json(name = "profile_url")
    val profile_url: String?
)