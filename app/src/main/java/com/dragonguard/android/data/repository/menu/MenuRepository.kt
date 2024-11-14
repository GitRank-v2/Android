package com.dragonguard.android.data.repository.menu

import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleAdminApi
import com.dragonguard.android.util.handleWithdrawApi
import javax.inject.Inject

class MenuRepository @Inject constructor(private val service: GitRankService) {
    suspend fun checkAdmin(): DataResult<Boolean> {
        return handleAdminApi { service.getPermissionState() }
    }

    suspend fun withDrawAccount(): DataResult<Boolean> {
        return handleWithdrawApi { service.postWithDraw() }
    }
}