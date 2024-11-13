package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApproveRequestOrgModelItem(
    @field:Json(name = "id")
    val id: Long,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "type")
    val type: String,
    @field:Json(name = "email_endpoint")
    val email_endpoint: String
)