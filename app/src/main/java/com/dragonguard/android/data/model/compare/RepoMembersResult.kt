package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Repository의 멤버의 상세정보를 담기 위한 모델
@JsonClass(generateAdapter = true)
data class RepoMembersResult(
    @field:Json(name = "additions")
    val additions: Int,
    @field:Json(name = "commits")
    val commits: Int,
    @field:Json(name = "deletions")
    val deletions: Int,
    @field:Json(name = "github_id")
    val github_id: String,
    @field:Json(name = "profile_url")
    val profile_url: String?,
    @field:Json(name = "is_service_member")
    val is_service_member: Boolean
)