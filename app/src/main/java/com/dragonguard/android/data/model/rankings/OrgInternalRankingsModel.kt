package com.dragonguard.android.data.model.rankings

data class OrgInternalRankingsModel(
    val github_id: String?,
    val id: Long?,
    val tier: String?,
    val tokens: Int?,
    val ranking: Int,
    val profile_image: String
)
