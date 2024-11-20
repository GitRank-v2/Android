package com.dragonguard.android.data.repository.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class RepoCompareRepositoryImpl @Inject constructor(private val service: GitRankService) :
    RepoCompareRepository {
    override suspend fun postCompareRepoMembersRequest(body: CompareRepoRequestModel): DataResult<CompareRepoMembersResponseModel> {
        return handleApi({ service.postCompareRepoMembers(body) }) { it.data }
    }

    override suspend fun postCompareRepoRequest(body: CompareRepoRequestModel): DataResult<CompareRepoResponseModel> {
        return handleApi({ service.postCompareRepo(body) }) { it.data }
    }
}