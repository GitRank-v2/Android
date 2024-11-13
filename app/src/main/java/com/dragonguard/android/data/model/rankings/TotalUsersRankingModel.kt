package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 모든 사용자의 정보를 랭킹순서대로 받기위해 정의한 model
 1등부터 끝 순서로 받아오며 TotalUsersRankingModelItem 의 리스트이다
 */
@JsonClass(generateAdapter = true)
data class TotalUsersRankingModel(
    @field:Json(name = "ranks")
    val ranks: List<TotalUsersRankingModelItem>
)