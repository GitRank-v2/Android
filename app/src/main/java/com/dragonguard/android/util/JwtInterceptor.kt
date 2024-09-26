package com.dragonguard.android.util

import com.dragonguard.android.GitRankApplication.Companion.getPref
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${getPref().getJwtToken("")}")
            .build()
        return chain.proceed(newRequest)
    }
}