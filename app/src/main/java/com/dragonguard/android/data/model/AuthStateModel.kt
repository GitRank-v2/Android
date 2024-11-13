package com.dragonguard.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthStateModel(
    @field:Json(name = "is_login_user")
    val is_login_user: Boolean?
)