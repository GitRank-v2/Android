package com.dragonguard.android.util

import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getString
import com.dragonguard.android.R
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.format

class JwtInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader(
                getString(R.string.auth),
                format(getString(R.string.bearer), getPref().getJwtToken(""))
            )
            .build()
        return chain.proceed(newRequest)
    }
}