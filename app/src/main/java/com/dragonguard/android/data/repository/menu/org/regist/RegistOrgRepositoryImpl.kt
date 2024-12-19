package com.dragonguard.android.data.repository.menu.org.regist

import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class RegistOrgRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    RegistOrgRepository {
    override suspend fun postRegistOrg(body: RegistOrgModel): DataResult<RegistOrgResultModel> {
        return handleApi({ service.postOrgRegist(body) }, retrofit) { it.data }
    }
}