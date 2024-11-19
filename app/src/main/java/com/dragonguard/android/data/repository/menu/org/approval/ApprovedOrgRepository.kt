package com.dragonguard.android.data.repository.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.util.DataResult

interface ApprovedOrgRepository {
    suspend fun statusOrgList(status: String, page: Int): DataResult<ApproveRequestOrgModel>
}