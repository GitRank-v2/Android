package com.dragonguard.android.data.repository.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.util.DataResult

interface SearchRepository {
    suspend fun getUserNames(
        name: String,
        count: Int,
        type: String
    ): DataResult<List<UserNameModelItem>>

    suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<List<RepoSearchResultModel>>

    suspend fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
    ): DataResult<List<RepoSearchResultModel>>
}