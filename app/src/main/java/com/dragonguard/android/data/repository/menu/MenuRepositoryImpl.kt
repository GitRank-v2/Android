package com.dragonguard.android.data.repository.menu

import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleAdminApi
import com.dragonguard.android.util.handleWithdrawApi
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(private val service: GitRankService) : MenuRepository {
    override suspend fun checkAdmin(): DataResult<Boolean> {
        return handleAdminApi { service.getPermissionState() }
    }

    override suspend fun withDrawAccount(): DataResult<Boolean> {
        return handleWithdrawApi { service.postWithDraw() }
    }
}