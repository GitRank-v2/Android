package com.dragonguard.android.data.repository.search.repo

import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class RepoContributorsRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    RepoContributorsRepository {
    override suspend fun getRepoContributors(repoName: String): DataResult<RepoContributorsModel> {
        return handleApi({ service.getRepoContributors(repoName) }, retrofit) { it.data }
    }
}