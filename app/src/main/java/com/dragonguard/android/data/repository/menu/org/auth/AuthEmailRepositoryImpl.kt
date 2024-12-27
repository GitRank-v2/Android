package com.dragonguard.android.data.repository.menu.org.auth

import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class AuthEmailRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    AuthEmailRepository {
    override suspend fun addOrgMember(body: AddOrgMemberModel): DataResult<Long> {
        return handleApi({ service.postAddOrgMember(body) }) { it.data.id }
    }

    override suspend fun emailAuthResult(id: Long, code: String, orgId: Long): DataResult<Boolean> {
        val queryMap = mutableMapOf<String, String>()

        queryMap.put("code", code)
        queryMap.put("organizationId", orgId.toString())
        return handleApi(
            { service.getEmailAuthResult(id.toString(), queryMap) },
            retrofit
        ) { it.data.is_valid_code }
    }

    override suspend fun deleteEmailCode(id: Long): DataResult<Unit> {
        return handleApi({ service.deleteEmailCode(id) }) { it }
    }

    override suspend fun sendEmailAuth(id: Long): DataResult<Long> {
        return handleApi({ service.postResendEmail(id) }) { it.data.id }
    }
}