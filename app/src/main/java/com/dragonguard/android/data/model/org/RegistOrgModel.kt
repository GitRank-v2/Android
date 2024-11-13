package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistOrgModel(
    @field:Json(name = "email_endpoint")
    val email_endpoint: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "organization_type")
    val organization_type: String
)