package com.dragonguard.android.data.model.org

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrganizationNamesModel(
    @field:Json(name = "data")
    var data: List<OrganizationNamesModelItem>
) {
    constructor() : this(emptyList())
}