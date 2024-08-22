package com.dragonguard.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import com.dragonguard.android.util.IdPreference

class GitRankApplication : Application() {
    override fun onCreate() {
        super<Application>.onCreate()
        context = applicationContext
        pref = IdPreference(context)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private lateinit var pref: IdPreference

        fun getString(@StringRes stringResId: Int): String {
            return context.getString(stringResId)
        }

        fun getPref(): IdPreference = pref
    }
}