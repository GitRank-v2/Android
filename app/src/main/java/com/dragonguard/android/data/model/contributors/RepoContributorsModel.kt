package com.dragonguard.android.data.model.contributors

data class RepoContributorsModel(
    val git_repo_members: List<GitRepoMember>?,
    val spark_line: List<Int>?
) {
    constructor() : this(
        git_repo_members = listOf(),
        spark_line = listOf()
    )
}