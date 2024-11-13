package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TotalOrganizationModel(
    @field:Json(name = "email_endpoint")
    override val email_endpoint: String?,
    @field:Json(name = "id")
    override val id: Long?,
    @field:Json(name = "name")
    override val name: String?,
    @field:Json(name = "organization_type")
    override val organization_type: String?,
    @field:Json(name = "token_sum")
    override val token_sum: Long?,
    @field:Json(name = "ranking")
    var ranking: Int? = 0
) : OrganizationRankingModelItem(email_endpoint, id, name, organization_type, token_sum)
