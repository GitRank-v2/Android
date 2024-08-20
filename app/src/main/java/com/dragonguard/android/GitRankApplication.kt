package com.dragonguard.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.StringRes

class GitRankApplication : Application() {
    override fun onCreate() {
        super<Application>.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getString(@StringRes stringResId: Int): String {
            return context.getString(stringResId)
        }
    }
}