package com.dragonguard.android.data.repository.compare.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.util.DataResult

interface CompareSearchRepository {
    suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<List<RepoSearchResultModel>>
}