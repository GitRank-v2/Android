package com.dragonguard.android.data.model.rankings

/*
 모든 사용자의 랭킹을 받기위해 정의한 model
 */
abstract class TotalUsersRankingModelItem(
    open var tokens: Long?,
    open var github_id: String?,
    open var id: String?,
    open var name: String?,
    open var tier: String?,
    open var profile_image: String?
)