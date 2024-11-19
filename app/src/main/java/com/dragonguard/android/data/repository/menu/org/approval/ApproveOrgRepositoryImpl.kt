package com.dragonguard.android.data.repository.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
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
        return handleApi({ service.getOrgStatus(queryMap) }) { it }
    }

    override suspend fun approveOrgRequest(body: OrgApprovalModel): DataResult<ApproveRequestOrgModel> {
        return handleApi({ service.postOrgApproval(body) }) { it }
    }
}