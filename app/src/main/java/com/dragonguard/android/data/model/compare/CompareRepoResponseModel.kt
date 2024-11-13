package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//두 Repository의 비교 결과를 받기 위한 모델
@JsonClass(generateAdapter = true)
data class CompareRepoResponseModel(
    @field:Json(name = "first_repo")
    val first_repo: RepoStats?,
    @field:Json(name = "second_repo")
    val second_repo: RepoStats?
)