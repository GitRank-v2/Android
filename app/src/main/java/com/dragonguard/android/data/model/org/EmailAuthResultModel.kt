package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmailAuthResultModel(
    @field:Json(name = "is_valid_code")
    val is_valid_code: Boolean
)