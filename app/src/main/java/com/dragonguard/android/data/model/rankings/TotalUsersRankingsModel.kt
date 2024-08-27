package com.dragonguard.android.data.model.rankings

// 유저들의 랭킹 대입을 쉽게 하기 위한 Model
data class TotalUsersRankingsModel(
    override var tokens: Long?,
    override var github_id: String?,
    override var id: String?,
    override var name: String?,
    override var tier: String?,
    var ranking: Int? = 0,
    override var profile_image: String?
) : TotalUsersRankingModelItem(tokens, github_id, id, name, tier, profile_image)
