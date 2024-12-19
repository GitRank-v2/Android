package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrgInternalRankingModelItem(
    @field:Json(name = "github_id")
    val github_id: String?,
    @field:Json(name = "id")
    val id: String?,
    @field:Json(name = "tier")
    val tier: String?,
    @field:Json(name = "contribution_amount")
    val contribution_amount: Int?,
    @field:Json(name = "profile_image")
    val profile_image: String
)