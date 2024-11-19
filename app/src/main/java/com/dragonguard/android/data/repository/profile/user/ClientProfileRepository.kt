package com.dragonguard.android.data.repository.profile.user

import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.util.DataResult

interface ClientProfileRepository {
    suspend fun getClientDetails(): DataResult<ClientDetailModel>
}