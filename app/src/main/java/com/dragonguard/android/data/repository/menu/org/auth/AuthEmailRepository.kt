package com.dragonguard.android.data.repository.menu.org.auth

import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.util.DataResult

interface AuthEmailRepository {
    suspend fun addOrgMember(body: AddOrgMemberModel): DataResult<Long>
    suspend fun emailAuthResult(id: Long, code: String, orgId: Long): DataResult<Boolean>
    suspend fun deleteEmailCode(id: Long): DataResult<Unit>
    suspend fun sendEmailAuth(): DataResult<Long>
}