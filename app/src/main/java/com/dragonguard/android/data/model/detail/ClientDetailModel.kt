package com.dragonguard.android.data.model.detail

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClientDetailModel(
    @field:Json(name = "git_organizations")
    val git_organizations: List<GitOrganization>,
    @field:Json(name = "git_repos")
    val git_repos: List<String>,
    @field:Json(name = "member_profile_image")
    val member_profile_image: String
) {
    constructor() : this(
        listOf(),
        listOf(),
        ""
    )
}