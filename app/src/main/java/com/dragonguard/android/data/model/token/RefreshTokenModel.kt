package com.dragonguard.android.data.model.token

data class RefreshTokenModel(
    val access_token: String?,
    val grant_type: String?,
    val refresh_token: String?
)