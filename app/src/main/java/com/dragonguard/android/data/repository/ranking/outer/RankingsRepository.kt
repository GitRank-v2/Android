package com.dragonguard.android.data.repository.ranking.outer

import com.dragonguard.android.data.model.rankings.OrganizationRankingModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModel
import com.dragonguard.android.util.DataResult

interface RankingsRepository {
    suspend fun getTotalUsersRankings(
        page: Int,
        size: Int
    ): DataResult<TotalUsersRankingModel>

    suspend fun allOrgRanking(page: Int): DataResult<OrganizationRankingModel>
    suspend fun typeOrgRanking(type: String, page: Int): DataResult<OrganizationRankingModel>
}