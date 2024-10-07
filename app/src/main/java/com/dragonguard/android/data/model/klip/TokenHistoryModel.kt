package com.dragonguard.android.data.model.klip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//토큰 부여 기록을 받아오기위한 arraylist
@JsonClass(generateAdapter = true)
data class TokenHistoryModel(
    @field:Json(name = "data")
    val data: List<TokenHistoryModelItem>
)