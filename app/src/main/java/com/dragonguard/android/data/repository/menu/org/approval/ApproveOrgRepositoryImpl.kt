package com.dragonguard.android.data.repository.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class ApproveOrgRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    ApproveOrgRepository {
    override suspend fun statusOrgList(
        status: String,
        page: Int
    ): DataResult<List<ApproveRequestOrgModelItem>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("status", status)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgStatus(queryMap) }, retrofit) { it.data }
    }

    override suspend fun approveOrgRequest(
        id: Long,
        body: OrgApprovalModel
    ): DataResult<List<ApproveRequestOrgModelItem>> {
        return handleApi({ service.postOrgApproval(id, body) }) { it.data }
    }
}