package com.dragonguard.android.data.model.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserNameModelItem(
    @field:Json(name = "github_id")
    val github_id: String,
    @field:Json(name = "is_service_member")
    val is_service_member: Boolean
) {
    fun compare(other: UserNameModelItem): Boolean {
        return this.github_id == other.github_id &&
                this.is_service_member == other.is_service_member
    }
}