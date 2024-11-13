package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApproveRequestOrgModel(
    @field:Json(name = "data")
    val data: List<ApproveRequestOrgModelItem>
) {
    constructor() : this(listOf())
}