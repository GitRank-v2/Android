package com.dragonguard.android.data.repository.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.util.DataResult

interface SearchOrganizationRepository {
    suspend fun getOrgNames(
        name: String,
        count: Int,
        type: String
    ): DataResult<OrganizationNamesModel>
}