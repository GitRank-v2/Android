package com.dragonguard.android.data.model.main

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserContributionsModel(
    @field:Json(name = "contribute_type")
    val contribute_type: String,
    @field:Json(name = "amount")
    val amount: Long,
    @field:Json(name = "created_at")
    val created_at: String
)
