package com.dragonguard.android.data.model.main

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 메인화면의 사용자의 프로필, 랭킹, 기여도, 티어등을
 받기위해 정의한 model
 */
@JsonClass(generateAdapter = true)
data class UserInfoModel(
    @field:Json(name = "auth_step")
    var auth_step: String? = "",
    @field:Json(name = "commits")
    var commits: Int? = 0,
    @field:Json(name = "github_id")
    var github_id: String? = "",
    @field:Json(name = "id")
    var id: String? = "",
    @field:Json(name = "name")
    var name: String? = "",
    @field:Json(name = "profile_image")
    var profile_image: String? = "",
    @field:Json(name = "tier")
    var tier: String? = "",
    @field:Json(name = "rank")
    var rank: Int? = 0,
    @field:Json(name = "token_amount")
    var token_amount: Int? = 0,
    @field:Json(name = "organization")
    var organization: String? = "",
    @field:Json(name = "organization_rank")
    var organization_rank: Int? = 0,
    @field:Json(name = "blockchain_url")
    var blockchain_url: String? = "",
    @field:Json(name = "issues")
    var issues: Int? = 0,
    @field:Json(name = "pull_requests")
    var pull_requests: Int? = 0,
    @field:Json(name = "reviews")
    var reviews: Int? = 0,
    @field:Json(name = "is_last")
    var is_last: Boolean? = false,
    @field:Json(name = "member_github_ids")
    var member_github_ids: List<String>? = listOf()
) {
    constructor() : this(
        auth_step = "",
        commits = 0,
        github_id = "",
        id = "",
        name = "",
        profile_image = "",
        tier = "",
        rank = 0,
        token_amount = 0,
        organization = "",
        organization_rank = 0,
        blockchain_url = "",
        issues = 0,
        pull_requests = 0,
        reviews = 0,
        is_last = false,
        member_github_ids = listOf()
    )
}