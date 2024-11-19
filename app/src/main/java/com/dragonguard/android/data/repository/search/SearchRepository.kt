package com.dragonguard.android.data.repository.search

import com.dragonguard.android.data.model.search.RepoNameModel
import com.dragonguard.android.data.model.search.UserNameModel
import com.dragonguard.android.util.DataResult

interface SearchRepository {
    suspend fun getUserNames(name: String, count: Int, type: String): DataResult<UserNameModel>
    suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<RepoNameModel>

    suspend fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
    ): DataResult<RepoNameModel>
}