package com.dragonguard.android.data.model.contributors

data class RepoContributorsModel(
    val git_repo_members: List<GitRepoMember>?,
    val spark_line: List<Int>?
)