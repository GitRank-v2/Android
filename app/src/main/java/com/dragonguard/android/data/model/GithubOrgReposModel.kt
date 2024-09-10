package com.dragonguard.android.data.model

data class GithubOrgReposModel(
    val git_repos: List<String>,
    val profile_image: String
) {
    constructor() : this(
        git_repos = listOf(),
        profile_image = ""
    )
}