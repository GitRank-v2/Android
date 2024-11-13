package com.dragonguard.android.data.model.detail

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitOrganization(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "profile_image")
    val profile_image: String
)