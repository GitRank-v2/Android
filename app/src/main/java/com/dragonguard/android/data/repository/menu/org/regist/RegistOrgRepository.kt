package com.dragonguard.android.data.repository.menu.org.regist

import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.util.DataResult

interface RegistOrgRepository {
    suspend fun postRegistOrg(body: RegistOrgModel): DataResult<RegistOrgResultModel>
}