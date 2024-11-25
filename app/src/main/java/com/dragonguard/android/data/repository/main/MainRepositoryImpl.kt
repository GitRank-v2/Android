package com.dragonguard.android.data.repository.main

import com.dragonguard.android.data.model.main.UserContributionsModel
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) : MainRepository {
    override suspend fun getUserInfo(): DataResult<UserInfoModel> {
        return handleApi({ service.getUserInfo() }, retrofit) { it }
    }

    override suspend fun getNewAccessToken(
        access: String,
        refresh: String
    ): DataResult<RefreshTokenModel> {
        return handleApi({ service.getNewAccessToken(access, refresh) }) { it.data }
    }

    override suspend fun updateGitContributions(): DataResult<List<UserContributionsModel>> {
        return handleApi({ service.updateGitContributions() }, retrofit) { it.data }
    }

    override suspend fun updateGitContribution(): DataResult<Unit> {
        return handleApi({ service.updateGitContribution() }, retrofit) { it }
    }
}