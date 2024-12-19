package com.dragonguard.android.data.repository.ranking.outer

import com.dragonguard.android.data.model.rankings.OrganizationRankingModelItem
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModelItem
import com.dragonguard.android.util.DataResult

interface RankingsRepository {
    suspend fun getTotalUsersRankings(
        page: Int,
        size: Int
    ): DataResult<List<TotalUsersRankingModelItem>>

    suspend fun allOrgRanking(page: Int): DataResult<List<OrganizationRankingModelItem>>
    suspend fun typeOrgRanking(
        type: String,
        page: Int
    ): DataResult<List<OrganizationRankingModelItem>>
}