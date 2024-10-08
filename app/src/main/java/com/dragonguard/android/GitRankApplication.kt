package com.dragonguard.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.JwtInterceptor
import com.dragonguard.android.util.NetworkChecker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class GitRankApplication : Application(), DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()
        context = applicationContext
        pref = IdPreference(context)
        networkConnectionChecker = NetworkChecker(context)
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        retrofit = Retrofit.Builder().baseUrl(Companion.getString(R.string.base_url))
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        service = retrofit.create(GitRankService::class.java)
        repository = ApiRepository()
    }

    override fun onStop(owner: LifecycleOwner) {
        networkConnectionChecker.unregister()
        super.onStop(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        networkConnectionChecker.register()
        super.onStart(owner)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private lateinit var moshi: Moshi

        private var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(18, TimeUnit.SECONDS)
            .writeTimeout(18, TimeUnit.SECONDS)
            .addInterceptor(JwtInterceptor())
            .retryOnConnectionFailure(true)
            .build()

        private lateinit var retrofit: Retrofit
        private lateinit var repository: ApiRepository

        private lateinit var pref: IdPreference


        private lateinit var service: GitRankService

        private lateinit var networkConnectionChecker: NetworkChecker
        fun isOnline() = networkConnectionChecker.isOnline()

        fun getString(@StringRes stringResId: Int): String {
            return context.getString(stringResId)
        }

        fun getPref(): IdPreference = pref
        fun getRepository(): ApiRepository = repository
        fun getService(): GitRankService = service
        fun setService() {
            Log.d("setService", "setService: ")
            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(18, TimeUnit.SECONDS)
                .writeTimeout(18, TimeUnit.SECONDS)
                .addInterceptor(JwtInterceptor())
                .retryOnConnectionFailure(true)
                .build()
            retrofit = Retrofit.Builder().baseUrl(getString(R.string.base_url))
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            service = retrofit.create(GitRankService::class.java)
        }
    }
}