package com.dragonguard.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.util.IdPreference

class GitRankApplication : Application() {
    override fun onCreate() {
        super<Application>.onCreate()
        context = applicationContext
        pref = IdPreference(context)
        repository = ApiRepository()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private lateinit var pref: IdPreference
        private lateinit var repository: ApiRepository

        fun getString(@StringRes stringResId: Int): String {
            return context.getString(stringResId)
        }

        fun getPref(): IdPreference = pref
        fun getRepository(): ApiRepository = repository
    }
}