package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistOrgResultModel(
    @field:Json(name = "id")
    val id: Long
) {
    constructor() : this(0)
}