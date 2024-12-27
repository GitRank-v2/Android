package com.dragonguard.android.data.repository.login

import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.util.DataResult

interface LoginRepository {
    suspend fun refreshToken(access: String, refresh: String): DataResult<RefreshTokenModel>
}