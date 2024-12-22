package com.dragonguard.android.data.repository.search

import android.util.Log
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    SearchRepository {
    override suspend fun getUserNames(
        name: String,
        count: Int,
        type: String
    ): DataResult<List<UserNameModelItem>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("q", name)
        queryMap.put("type", type)
        return handleApi({ service.getUserName(queryMap) }, retrofit) { it.data }
    }

    override suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<List<RepoSearchResultModel>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("q", name)
        queryMap.put("type", type)
        return handleApi({ service.getRepoName(queryMap) }, retrofit) { it.data }

    }

    override suspend fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
    ): DataResult<List<RepoSearchResultModel>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("q", name)
        queryMap.put("type", type)
        queryMap.put("filters", filters)
        Log.d("api 호출", "이름: $name, type: $type filters: $filters")
        Log.d("api 호출", "$count 페이지 검색")
        return handleApi({ service.getRepoName(queryMap) }, retrofit) { it.data }
    }
}