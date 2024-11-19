package com.dragonguard.android.data.repository.menu

import com.dragonguard.android.util.DataResult

interface MenuRepository {
    suspend fun checkAdmin(): DataResult<Boolean>
    suspend fun withDrawAccount(): DataResult<Boolean>
}