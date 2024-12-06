package com.dragonguard.android.data.repository.compare.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class CompareSearchRepositoryImpl @Inject constructor(private val service: GitRankService) :
    CompareSearchRepository {
    override suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<List<RepoSearchResultModel>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        return handleApi({ service.getRepoName(queryMap) }) { it.data }

    }
}