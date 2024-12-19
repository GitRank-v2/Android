package com.dragonguard.android.data.repository.ranking

import com.dragonguard.android.data.model.rankings.OrgInternalRankingModelItem
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class OrganizationInternalRepositoryImpl @Inject constructor(private val service: GitRankService) :
    OrganizationInternalRepository {
    override suspend fun searchOrgId(orgName: String): DataResult<Long> {
        return handleApi({ service.getOrgId(orgName) }) { it.data.id }
    }

    override suspend fun orgInternalRankings(
        id: Long,
        count: Int
    ): DataResult<List<OrgInternalRankingModelItem>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", count.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgInternalRankings(id.toString(), queryMap) }) { it.data }
    }
}