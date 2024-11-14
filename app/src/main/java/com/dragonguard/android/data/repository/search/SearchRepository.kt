package com.dragonguard.android.data.repository.search

import android.util.Log
import com.dragonguard.android.data.model.search.RepoNameModel
import com.dragonguard.android.data.model.search.UserNameModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class SearchRepository @Inject constructor(private val service: GitRankService) {
    suspend fun getUserNames(name: String, count: Int, type: String): DataResult<UserNameModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        return handleApi({ service.getUserName(queryMap) }) { it }
    }

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

    suspend fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
    ): DataResult<RepoNameModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("filters", filters)
        Log.d("api 호출", "이름: $name, type: $type filters: $filters")
        Log.d("api 호출", "$count 페이지 검색")
        return handleApi({ service.getRepoName(queryMap) }) { it }
    }
}