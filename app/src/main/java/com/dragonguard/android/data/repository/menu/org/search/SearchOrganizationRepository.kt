package com.dragonguard.android.data.repository.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModelItem
import com.dragonguard.android.util.DataResult

interface SearchOrganizationRepository {
    suspend fun getOrgNames(
        name: String,
        page: Int,
        type: String
    ): DataResult<List<OrganizationNamesModelItem>>

    suspend fun getOrgNames(
        page: Int,
        type: String
    ): DataResult<List<OrganizationNamesModelItem>>
}