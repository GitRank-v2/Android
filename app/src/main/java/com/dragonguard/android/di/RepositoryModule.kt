package com.dragonguard.android.di

import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.data.repository.main.MainRepository
import com.dragonguard.android.data.service.GitRankService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideMainRepository(service: GitRankService): MainRepository =
        MainRepository(service)

    @Singleton
    @Provides
    fun provideApiRepository(service: GitRankService): ApiRepository =
        ApiRepository(service)

}