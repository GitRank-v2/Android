package com.dragonguard.android.data.model.klip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//토큰 부여 기록을 받아오기위한 모델
@JsonClass(generateAdapter = true)
data class TokenHistoryModelItem(
    @field:Json(name = "amount")
    val amount: Int?,
    @field:Json(name = "contribute_type")
    val contribute_type: String?,
    @field:Json(name = "github_id")
    val github_id: String?,
    @field:Json(name = "id")
    val id: Long?,
    @field:Json(name = "member_id")
    val member_id: String?,
    @field:Json(name = "created_at")
    val created_at: String?,
    @field:Json(name = "transaction_hash_url")
    val transaction_hash_url: String?
)