package com.dragonguard.android.data.repository.profile.user

import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class ClientReposRepositoryImpl @Inject constructor(private val service: GitRankService) :
    ClientReposRepository {
    override suspend fun userGitOrgRepoList(orgName: String): DataResult<GithubOrgReposModel> {
        return handleApi({ service.getOrgRepoList(orgName) }) { it.data }
    }
}