package com.dragonguard.android.data.repository.search.repo

import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.util.DataResult

interface RepoContributorsRepository {
    suspend fun getRepoContributors(repoName: String): DataResult<RepoContributorsModel>
}