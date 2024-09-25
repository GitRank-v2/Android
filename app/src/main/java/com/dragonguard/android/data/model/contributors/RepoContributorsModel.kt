package com.dragonguard.android.data.model.contributors

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepoContributorsModel(
    @field:Json(name = "git_repo_members")
    val git_repo_members: List<GitRepoMember>?,
    @field:Json(name = "spark_line")
    val spark_line: List<Int>?
) {
    constructor() : this(
        git_repo_members = listOf(),
        spark_line = listOf()
    )
}