package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrgApprovalModel(
    @field:Json(name = "decide")
    val organization_status: String,
)