package com.dragonguard.android.data.model

data class StandardResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)