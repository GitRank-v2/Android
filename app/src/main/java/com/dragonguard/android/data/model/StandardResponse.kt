package com.dragonguard.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StandardResponse<T>(
    @field:Json(name = "code")
    val code: Int,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: T
)