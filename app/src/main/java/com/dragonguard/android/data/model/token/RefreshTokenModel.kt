package com.dragonguard.android.data.model.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshTokenModel(
    @field:Json(name = "access_token")
    val access_token: String?,
    @field:Json(name = "grant_type")
    val grant_type: String?,
    @field:Json(name = "refresh_token")
    val refresh_token: String?
)