package com.dragonguard.android.data.model.rankings

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OrganizationRankingModel : ArrayList<OrganizationRankingModelItem>()