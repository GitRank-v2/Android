package com.dragonguard.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.NetworkChecker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitRankApplication : Application(), DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()
        context = applicationContext
        networkConnectionChecker = NetworkChecker(context)
        pref = IdPreference(context)
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

        private lateinit var pref: IdPreference
        fun getPref(): IdPreference = pref

        private lateinit var networkConnectionChecker: NetworkChecker
        fun isOnline() = networkConnectionChecker.isOnline()

        fun getString(@StringRes stringResId: Int): String {
            return context.getString(stringResId)
        }
    }
}