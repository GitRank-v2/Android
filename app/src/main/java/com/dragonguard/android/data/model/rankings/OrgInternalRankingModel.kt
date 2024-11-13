package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrgInternalRankingModel(
    @field:Json(name = "data")
    val data: List<OrgInternalRankingModelItem>
) {
    constructor() : this(emptyList())
}