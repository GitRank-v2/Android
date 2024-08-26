package com.dragonguard.android.data.model.rankings

data class TotalOrganizationModel(
    override val email_endpoint: String?,
    override val id: Long?,
    override val name: String?,
    override val organization_type: String?,
    override val token_sum: Long?,
    val ranking: Int?
): OrganizationRankingModelItem(email_endpoint, id, name, organization_type, token_sum)
