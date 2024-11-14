package com.dragonguard.android.data.repository.compare.search

import com.dragonguard.android.data.model.search.RepoNameModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class CompareSearchRepository @Inject constructor(private val service: GitRankService) {
    suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<RepoNameModel> {

        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        return handleApi({ service.getRepoName(queryMap) }) { it }

    }
}