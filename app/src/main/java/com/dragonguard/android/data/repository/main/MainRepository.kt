package com.dragonguard.android.data.repository.main

import com.dragonguard.android.data.model.main.UserContributionsModel
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.util.DataResult

interface MainRepository {
    suspend fun getUserInfo(): DataResult<UserInfoModel>
    suspend fun updateGitContributions(): DataResult<List<UserContributionsModel>>
    suspend fun updateGitContribution(): DataResult<Unit>
}