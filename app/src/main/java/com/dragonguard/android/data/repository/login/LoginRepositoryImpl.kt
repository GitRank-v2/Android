package com.dragonguard.android.data.repository.login

import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) : LoginRepository {
    override suspend fun refreshToken(
        access: String,
        refresh: String
    ): DataResult<RefreshTokenModel> {
        return handleApi({ service.getNewAccessToken(access, refresh) }, retrofit) { it.data }
    }
}