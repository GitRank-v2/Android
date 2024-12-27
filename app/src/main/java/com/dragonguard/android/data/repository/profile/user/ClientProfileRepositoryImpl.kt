package com.dragonguard.android.data.repository.profile.user

import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.model.detail.GitOrganization
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class ClientProfileRepositoryImpl @Inject constructor(private val service: GitRankService) :
    ClientProfileRepository {
    override suspend fun getClientDetails(): DataResult<ClientDetailModel> {
        return handleApi({ service.getMemberDetails() }) { it.data }
    }

    override suspend fun getClientRepository(): DataResult<List<String>> {
        return handleApi({ service.getClientRepository() }) { it.data }
    }

    override suspend fun getClientOrg(): DataResult<List<GitOrganization>> {
        return handleApi({ service.getClientOrganization() }) { it.data }
    }
}