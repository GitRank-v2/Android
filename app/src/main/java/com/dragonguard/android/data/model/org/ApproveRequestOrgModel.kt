package com.dragonguard.android.data.model.org

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApproveRequestOrgModel : ArrayList<ApproveRequestOrgModelItem>()