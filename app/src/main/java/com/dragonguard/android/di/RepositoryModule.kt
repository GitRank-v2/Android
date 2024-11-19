package com.dragonguard.android.di

import com.dragonguard.android.data.repository.compare.compare.RepoCompareRepository
import com.dragonguard.android.data.repository.compare.compare.RepoCompareRepositoryImpl
import com.dragonguard.android.data.repository.compare.search.CompareSearchRepository
import com.dragonguard.android.data.repository.compare.search.CompareSearchRepositoryImpl
import com.dragonguard.android.data.repository.main.MainRepository
import com.dragonguard.android.data.repository.main.MainRepositoryImpl
import com.dragonguard.android.data.repository.menu.MenuRepository
import com.dragonguard.android.data.repository.menu.MenuRepositoryImpl
import com.dragonguard.android.data.repository.menu.org.approval.ApproveOrgRepository
import com.dragonguard.android.data.repository.menu.org.approval.ApproveOrgRepositoryImpl
import com.dragonguard.android.data.repository.menu.org.approval.ApprovedOrgRepository
import com.dragonguard.android.data.repository.menu.org.approval.ApprovedOrgRepositoryImpl
import com.dragonguard.android.data.repository.menu.org.auth.AuthEmailRepository
import com.dragonguard.android.data.repository.menu.org.auth.AuthEmailRepositoryImpl
import com.dragonguard.android.data.repository.menu.org.regist.RegistOrgRepository
import com.dragonguard.android.data.repository.menu.org.regist.RegistOrgRepositoryImpl
import com.dragonguard.android.data.repository.menu.org.search.SearchOrganizationRepository
import com.dragonguard.android.data.repository.menu.org.search.SearchOrganizationRepositoryImpl
import com.dragonguard.android.data.repository.profile.other.OthersProfileRepository
import com.dragonguard.android.data.repository.profile.other.OthersProfileRepositoryImpl
import com.dragonguard.android.data.repository.profile.user.ClientProfileRepository
import com.dragonguard.android.data.repository.profile.user.ClientProfileRepositoryImpl
import com.dragonguard.android.data.repository.profile.user.ClientReposRepository
import com.dragonguard.android.data.repository.profile.user.ClientReposRepositoryImpl
import com.dragonguard.android.data.repository.ranking.OrganizationInternalRepository
import com.dragonguard.android.data.repository.ranking.OrganizationInternalRepositoryImpl
import com.dragonguard.android.data.repository.ranking.outer.RankingsRepository
import com.dragonguard.android.data.repository.ranking.outer.RankingsRepositoryImpl
import com.dragonguard.android.data.repository.search.SearchRepository
import com.dragonguard.android.data.repository.search.SearchRepositoryImpl
import com.dragonguard.android.data.repository.search.repo.RepoContributorsRepository
import com.dragonguard.android.data.repository.search.repo.RepoContributorsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepoCompareRepository(repoCompareRepository: RepoCompareRepositoryImpl): RepoCompareRepository

    @Singleton
    @Binds
    abstract fun bindCompareSearchRepository(compareSearchRepository: CompareSearchRepositoryImpl): CompareSearchRepository

    @Singleton
    @Binds
    abstract fun bindMainRepository(mainRepository: MainRepositoryImpl): MainRepository

    @Singleton
    @Binds
    abstract fun provideMenuRepository(menuRepository: MenuRepositoryImpl): MenuRepository

    @Singleton
    @Binds
    abstract fun bindSearchOrganizationRepository(searchOrganizationRepository: SearchOrganizationRepositoryImpl): SearchOrganizationRepository

    @Singleton
    @Binds
    abstract fun funbindRegistOrgRepository(registOrgRepository: RegistOrgRepositoryImpl): RegistOrgRepository

    @Singleton
    @Binds
    abstract fun bindAuthEmailRepository(authEmailRepository: AuthEmailRepositoryImpl): AuthEmailRepository

    @Singleton
    @Binds
    abstract fun bindApprovedOrgRepository(approvedOrgRepository: ApprovedOrgRepositoryImpl): ApprovedOrgRepository

    @Singleton
    @Binds
    abstract fun bindApproveOrgRepository(approveOrgRepository: ApproveOrgRepositoryImpl): ApproveOrgRepository

    @Singleton
    @Binds
    abstract fun bindOthersProfileRepository(othersProfileRepository: OthersProfileRepositoryImpl): OthersProfileRepository

    @Singleton
    @Binds
    abstract fun bindClientProfileRepository(clientProfileRepository: ClientProfileRepositoryImpl): ClientProfileRepository

    @Singleton
    @Binds
    abstract fun bindClientReposRepository(clientReposRepository: ClientReposRepositoryImpl): ClientReposRepository

    @Singleton
    @Binds
    abstract fun bindOrganizationInternalRepository(organizationInternalRepository: OrganizationInternalRepositoryImpl): OrganizationInternalRepository

    @Singleton
    @Binds
    abstract fun bindRankingsRepository(rankingRepository: RankingsRepositoryImpl): RankingsRepository

    @Singleton
    @Binds
    abstract fun bindSearchRepository(searchRepository: SearchRepositoryImpl): SearchRepository

    @Singleton
    @Binds
    abstract fun repoContributorsRepository(repoContributorsRepository: RepoContributorsRepositoryImpl): RepoContributorsRepository
}