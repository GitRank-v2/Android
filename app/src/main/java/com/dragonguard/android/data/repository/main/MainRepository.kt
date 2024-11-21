package com.dragonguard.android.data.repository.main

import com.dragonguard.android.data.model.main.UserContributionsModel
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.util.DataResult

interface MainRepository {
    suspend fun getUserInfo(): DataResult<UserInfoModel>
    suspend fun getNewAccessToken(access: String, refresh: String): DataResult<RefreshTokenModel>
    suspend fun updateGitContributions(): DataResult<List<UserContributionsModel>>
}