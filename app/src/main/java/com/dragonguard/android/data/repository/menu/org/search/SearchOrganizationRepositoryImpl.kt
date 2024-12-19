package com.dragonguard.android.data.repository.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModelItem
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class SearchOrganizationRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    SearchOrganizationRepository {
    override suspend fun getOrgNames(
        name: String,
        page: Int,
        type: String
    ): DataResult<List<OrganizationNamesModelItem>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$page")
        queryMap.put("q", name)
        queryMap.put("type", type)
        queryMap.put("size", "10")
        return handleApi({ service.searchOrgNamesByName(queryMap) }, retrofit) { it.data }
    }

    override suspend fun getOrgNames(
        page: Int,
        type: String
    ): DataResult<List<OrganizationNamesModelItem>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$page")
        queryMap.put("type", type)
        queryMap.put("size", "10")
        return handleApi({ service.searchOrgNames(queryMap) }, retrofit) { it.data }
    }
}