package com.dragonguard.android.data.repository.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.util.DataResult

interface RepoCompareRepository {
    suspend fun postCompareRepoMembersRequest(body: CompareRepoRequestModel): DataResult<CompareRepoMembersResponseModel>
    suspend fun postCompareRepoRequest(body: CompareRepoRequestModel): DataResult<CompareRepoResponseModel>
}