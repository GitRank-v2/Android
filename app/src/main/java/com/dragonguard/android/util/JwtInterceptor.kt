package com.dragonguard.android.util

import com.dragonguard.android.GitRankApplication.Companion.getString
import com.dragonguard.android.R
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.format
import javax.inject.Inject

class JwtInterceptor @Inject constructor(private val preference: IdPreference) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader(
                getString(R.string.auth),
                format(getString(R.string.bearer), preference.getJwtToken(""))
            )
            .build()
        return chain.proceed(newRequest)
    }
}