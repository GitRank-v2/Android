package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddOrgMemberModel(
    @field:Json(name = "email")
    val email: String,
    @field:Json(name = "organization_id")
    val organization_id: Long
)