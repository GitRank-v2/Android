package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 유저들의 랭킹 대입을 쉽게 하기 위한 Model
@JsonClass(generateAdapter = true)
data class TotalUsersRankingsModel(
    @field:Json(name = "tokens")
    override var tokens: Long?,
    @field:Json(name = "github_id")
    override var github_id: String?,
    @field:Json(name = "id")
    override var id: String?,
    @field:Json(name = "name")
    override var name: String?,
    @field:Json(name = "tier")
    override var tier: String?,
    @field:Json(name = "rank")
    var ranking: Int? = 0,
    @field:Json(name = "profile_image")
    override var profile_image: String?
) : TotalUsersRankingModelItem(tokens, github_id, id, name, tier, profile_image)
