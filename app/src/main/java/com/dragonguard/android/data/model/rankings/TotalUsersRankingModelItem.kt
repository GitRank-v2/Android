package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 모든 사용자의 랭킹을 받기위해 정의한 model
 */
@JsonClass(generateAdapter = true)
data class TotalUsersRankingModelItem(
    @field:Json(name = "contribution_amount")
    var contribution_amount: Long?,
    @field:Json(name = "github_id")
    var github_id: String?,
    @field:Json(name = "id")
    var id: Long?,
    @field:Json(name = "tier")
    var tier: String?,
    @field:Json(name = "profile_image")
    var profile_image: String?
)