package com.dragonguard.android.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubOrgReposModel(
    @field:Json(name = "git_repos")
    val git_repos: List<String>,
    @field:Json(name = "profile_image")
    val profile_image: String
) {
    constructor() : this(
        git_repos = listOf(),
        profile_image = ""
    )
}