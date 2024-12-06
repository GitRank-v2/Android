package com.dragonguard.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorId(
    @field:Json(name = "error_id")
    val error_id: String
)
