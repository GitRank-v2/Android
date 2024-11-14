package com.dragonguard.android.data.repository.menu.org.auth

import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class AuthEmailRepository @Inject constructor(private val service: GitRankService) {
    suspend fun addOrgMember(body: AddOrgMemberModel): DataResult<Long> {
        return handleApi({ service.postAddOrgMember(body) }) { it.id }
    }

    suspend fun emailAuthResult(id: Long, code: String, orgId: Long): DataResult<Boolean> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("id", id.toString())
        queryMap.put("code", code)
        queryMap.put("organizationId", orgId.toString())
        return handleApi({ service.getEmailAuthResult(queryMap) }) { it.is_valid_code }
    }

    suspend fun deleteEmailCode(id: Long): DataResult<Unit> {
        return handleApi({ service.deleteEmailCode(id) }) { it }
    }

    suspend fun sendEmailAuth(): DataResult<Long> {
        return handleApi({ service.postAuthEmail() }) { it.id }
    }
}