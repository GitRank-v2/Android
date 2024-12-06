package com.dragonguard.android.data.repository.ranking.outer

import com.dragonguard.android.data.model.rankings.OrganizationRankingModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class RankingsRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    RankingsRepository {
    override suspend fun getTotalUsersRankings(
        page: Int,
        size: Int
    ): DataResult<TotalUsersRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$page")
        queryMap.put("size", "$size")
        queryMap.put("sort", "tokens,DESC")
        return handleApi({ service.getTotalUsersRanking(queryMap) }, retrofit) { it.data }
    }

    override suspend fun allOrgRanking(page: Int): DataResult<OrganizationRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getAllOrgRankings(queryMap) }, retrofit) { it.data }
    }

    override suspend fun typeOrgRanking(
        type: String,
        page: Int
    ): DataResult<OrganizationRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("type", type)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgRankings(queryMap) }, retrofit) { it.data }
    }
}