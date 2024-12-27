package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//두 Repository의 멤버의 상세정보를 담기 위한 모델
@JsonClass(generateAdapter = true)
data class CompareRepoMembersResponseModel(
    @field:Json(name = "first")
    val first: List<RepoMembersResult>?,
    @field:Json(name = "second")
    val second: List<RepoMembersResult>?
)