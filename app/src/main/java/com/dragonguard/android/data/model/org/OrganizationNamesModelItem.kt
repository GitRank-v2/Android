package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrganizationNamesModelItem(
    @field:Json(name = "email_endpoint")
    val email_endpoint: String?,
    @field:Json(name = "id")
    val id: Long?,
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "organization_type")
    val organization_type: String?,
    @field:Json(name = "token_sum")
    val token_sum: Int?
)