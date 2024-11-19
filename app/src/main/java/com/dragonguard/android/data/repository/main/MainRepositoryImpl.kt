package com.dragonguard.android.data.repository.main

import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val service: GitRankService) : MainRepository {
    override suspend fun getUserInfo(): DataResult<UserInfoModel> {
        return handleApi({ service.getUserInfo() }) { it }
    }

    override suspend fun getNewAccessToken(
        access: String,
        refresh: String
    ): DataResult<RefreshTokenModel> {
        return handleApi({ service.getNewAccessToken(access, refresh) }) { it }
    }

    override suspend fun updateGitContributions(): DataResult<Unit> {
        return handleApi({ service.updateGitContributions() }) { it }
    }
}