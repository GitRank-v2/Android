package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrganizationRankingModelItem(
    @field:Json(name = "id")
    val id: Long?,
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "contribution_amount")
    val contribution_amount: Long?,
    @field:Json(name = "type")
    val type: String?
)