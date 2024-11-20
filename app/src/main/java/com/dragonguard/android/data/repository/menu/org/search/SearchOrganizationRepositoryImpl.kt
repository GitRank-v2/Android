package com.dragonguard.android.data.repository.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class SearchOrganizationRepositoryImpl @Inject constructor(private val service: GitRankService) :
    SearchOrganizationRepository {
    override suspend fun getOrgNames(
        name: String,
        count: Int,
        type: String
    ): DataResult<OrganizationNamesModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$count")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("size", "10")
        return handleApi({ service.getOrgNames(queryMap) }) { it.data }
    }
}