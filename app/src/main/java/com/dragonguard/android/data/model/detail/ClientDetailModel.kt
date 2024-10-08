package com.dragonguard.android.data.model.detail

data class ClientDetailModel(
    val git_organizations: List<GitOrganization>,
    val git_repos: List<String>,
    val member_profile_image: String
) {
    constructor() : this(
        listOf(),
        listOf(),
        ""
    )
}