package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
abstract class OrganizationRankingModelItem(
    @field:Json(name = "email_endpoint")
    open val email_endpoint: String?,
    @field:Json(name = "id")
    open val id: Long?,
    @field:Json(name = "name")
    open val name: String?,
    @field:Json(name = "organization_type")
    open val organization_type: String?,
    @field:Json(name = "token_sum")
    open val token_sum: Long?
)