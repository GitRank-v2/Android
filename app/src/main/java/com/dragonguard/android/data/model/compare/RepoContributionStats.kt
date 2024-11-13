package com.dragonguard.android.data.model.compare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//addition, deletion, language의 stat을 담기 위한 모델
@JsonClass(generateAdapter = true)
data class RepoContributionStats(
    @field:Json(name = "average")
    val average: Double,
    @field:Json(name = "count")
    val count: Int,
    @field:Json(name = "max")
    val max: Int,
    @field:Json(name = "min")
    val min: Int,
    @field:Json(name = "sum")
    val sum: Int
)