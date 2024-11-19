package com.dragonguard.android.data.repository.main

import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.util.DataResult

interface MainRepository {
    suspend fun getUserInfo(): DataResult<UserInfoModel>
    suspend fun getNewAccessToken(access: String, refresh: String): DataResult<RefreshTokenModel>
    suspend fun updateGitContributions(): DataResult<Unit>
}