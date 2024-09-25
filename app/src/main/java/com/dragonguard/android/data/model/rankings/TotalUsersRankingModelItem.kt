package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 모든 사용자의 랭킹을 받기위해 정의한 model
 */
@JsonClass(generateAdapter = true)
abstract class TotalUsersRankingModelItem(
    @field:Json(name = "tokens")
    open var tokens: Long?,
    @field:Json(name = "github_id")
    open var github_id: String?,
    @field:Json(name = "id")
    open var id: String?,
    @field:Json(name = "name")
    open var name: String?,
    @field:Json(name = "tier")
    open var tier: String?,
    @field:Json(name = "profile_image")
    open var profile_image: String?
)