package com.dragonguard.android.data.repository.ranking

import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.util.DataResult

interface OrganizationInternalRepository {
    suspend fun searchOrgId(orgName: String): DataResult<Long>
    suspend fun orgInternalRankings(id: Long, count: Int): DataResult<OrgInternalRankingModel>
}