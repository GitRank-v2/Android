package com.dragonguard.android.data.repository.compare.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class CompareSearchRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    CompareSearchRepository {
    override suspend fun getRepositoryNames(
        name: String,
        count: Int
    ): DataResult<List<RepoSearchResultModel>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$count")
        queryMap.put("q", name)
        queryMap.put("type", "GIT_REPO")
        return handleApi({ service.getRepoName(queryMap) }, retrofit) { it.data }

    }
}