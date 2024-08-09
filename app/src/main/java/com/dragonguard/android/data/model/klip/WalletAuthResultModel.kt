package com.dragonguard.android.data.model.klip

//KLIP에서 지갑주소를 받아오는 모델
data class WalletAuthResultModel(
    val expiration_time: Int?,
    val request_key: String?,
    val result: com.dragonguard.android.data.model.klip.Result?,
    val status: String?
)