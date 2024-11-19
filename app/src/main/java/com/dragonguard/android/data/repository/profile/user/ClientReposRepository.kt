package com.dragonguard.android.data.repository.profile.user

import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.util.DataResult

interface ClientReposRepository {
    suspend fun userGitOrgRepoList(orgName: String): DataResult<GithubOrgReposModel>
}