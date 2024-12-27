package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//두 Repository의 비교 결과를 받기 위한 모델
@JsonClass(generateAdapter = true)
data class CompareRepoResponseModel(
    @field:Json(name = "first")
    val first: RepoStats?,
    @field:Json(name = "second")
    val second: RepoStats?
)