package com.dragonguard.android.di

import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.data.repository.compare.compare.RepoCompareRepository
import com.dragonguard.android.data.repository.compare.search.CompareSearchRepository
import com.dragonguard.android.data.repository.main.MainRepository
import com.dragonguard.android.data.repository.menu.MenuRepository
import com.dragonguard.android.data.repository.menu.org.approval.ApproveOrgRepository
import com.dragonguard.android.data.repository.menu.org.approval.ApprovedOrgRepository
import com.dragonguard.android.data.repository.menu.org.auth.AuthEmailRepository
import com.dragonguard.android.data.repository.menu.org.regist.RegistOrgRepository
import com.dragonguard.android.data.repository.menu.org.search.SearchOrganizationRepository
import com.dragonguard.android.data.repository.profile.other.OthersProfileRepository
import com.dragonguard.android.data.repository.profile.user.ClientProfileRepository
import com.dragonguard.android.data.repository.profile.user.ClientReposRepository
import com.dragonguard.android.data.repository.ranking.OrganizationInternalRepository
import com.dragonguard.android.data.repository.ranking.outer.RankingsRepository
import com.dragonguard.android.data.repository.search.SearchRepository
import com.dragonguard.android.data.repository.search.repo.RepoContributorsRepository
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
    fun provideCompareSearchRepository(service: GitRankService): CompareSearchRepository =
        CompareSearchRepository(service)

    @Singleton
    @Provides
    fun provideRepoCompareRepository(service: GitRankService): RepoCompareRepository =
        RepoCompareRepository(service)

    @Singleton
    @Provides
    fun provideMenuRepository(service: GitRankService): MenuRepository = MenuRepository(service)

    @Singleton
    @Provides
    fun provideSearchOrganizationRepository(service: GitRankService): SearchOrganizationRepository =
        SearchOrganizationRepository(service)

    @Singleton
    @Provides
    fun provideRegistOrgRepository(service: GitRankService): RegistOrgRepository =
        RegistOrgRepository(service)

    @Singleton
    @Provides
    fun provideAuthEmailRepository(service: GitRankService): AuthEmailRepository =
        AuthEmailRepository(service)

    @Singleton
    @Provides
    fun provideApprovedOrgRepository(service: GitRankService): ApprovedOrgRepository =
        ApprovedOrgRepository(service)

    @Singleton
    @Provides
    fun provideApproveOrgRepository(service: GitRankService): ApproveOrgRepository =
        ApproveOrgRepository(service)

    @Singleton
    @Provides
    fun provideOthersProfileRepository(service: GitRankService): OthersProfileRepository =
        OthersProfileRepository(service)

    @Singleton
    @Provides
    fun provideClientProfileRepository(service: GitRankService): ClientProfileRepository =
        ClientProfileRepository(service)

    @Singleton
    @Provides
    fun provideClientReposRepository(service: GitRankService): ClientReposRepository =
        ClientReposRepository(service)

    @Singleton
    @Provides
    fun provideOrganizationInternalRepository(service: GitRankService): OrganizationInternalRepository =
        OrganizationInternalRepository(service)

    @Singleton
    @Provides
    fun provideRankingsRepository(service: GitRankService): RankingsRepository =
        RankingsRepository(service)

    @Singleton
    @Provides
    fun provideSearchRepository(service: GitRankService): SearchRepository =
        SearchRepository(service)

    @Singleton
    @Provides
    fun provideRepoContributorsRepository(service: GitRankService): RepoContributorsRepository =
        RepoContributorsRepository(service)

    @Singleton
    @Provides
    fun provideApiRepository(service: GitRankService): ApiRepository =
        ApiRepository(service)


}