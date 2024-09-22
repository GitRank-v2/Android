package com.dragonguard.android.data.model.rankings

abstract class OrganizationRankingModelItem(
    open val email_endpoint: String?,
    open val id: Long?,
    open val name: String?,
    open val organization_type: String?,
    open val token_sum: Long?
)