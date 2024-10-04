package com.dragonguard.android.data.model.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserNameModel(
    @field:Json(name = "data")
    val data: List<UserNameModelItem>
)