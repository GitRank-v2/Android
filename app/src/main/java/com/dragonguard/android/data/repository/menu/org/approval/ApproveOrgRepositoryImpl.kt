package com.dragonguard.android.data.repository.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class ApproveOrgRepositoryImpl @Inject constructor(private val service: GitRankService) :
    ApproveOrgRepository {
    override suspend fun statusOrgList(
        status: String,
        page: Int
    ): DataResult<ApproveRequestOrgModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("status", status)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgStatus(queryMap) }) { it.data }
    }

    override suspend fun approveOrgRequest(
        id: Long,
        body: OrgApprovalModel
    ): DataResult<List<ApproveRequestOrgModelItem>> {
        return handleApi({ service.postOrgApproval(id, body) }) { it.data }
    }
}