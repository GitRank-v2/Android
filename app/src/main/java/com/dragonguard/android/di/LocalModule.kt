package com.dragonguard.android.di

import android.content.Context
import com.dragonguard.android.util.IdPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun provideIdPreference(@ApplicationContext context: Context): IdPreference =
        IdPreference(context)
}