package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//두 Repository 비교를 요청하기 위한 모델
@JsonClass(generateAdapter = true)
data class CompareRepoRequestModel(
    @field:Json(name = "first_repo")
    val first_repo: String,
    @field:Json(name = "second_repo")
    val second_repo: String
)