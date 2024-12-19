package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TotalOrganizationModel(
    @field:Json(name = "id")
    val id: Long?,
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "contribution_amount")
    val contribution_amount: Long?,
    @field:Json(name = "ranking")
    var ranking: Int? = 0,
    @field:Json(name = "type")
    val type: String?
)
