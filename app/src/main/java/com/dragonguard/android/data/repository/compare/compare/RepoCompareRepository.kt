package com.dragonguard.android.data.repository.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class RepoCompareRepository @Inject constructor(private val service: GitRankService) {
    //두 Repository의 구성원들의 기여도를 받아오기 위한 함수
    suspend fun postCompareRepoMembersRequest(body: CompareRepoRequestModel): DataResult<CompareRepoMembersResponseModel> {
        return handleApi({ service.postCompareRepoMembers(body) }) { it }
    }

    //두 Repository의 정보를 받아오기 위한 함수
    suspend fun postCompareRepoRequest(body: CompareRepoRequestModel): DataResult<CompareRepoResponseModel> {
        return handleApi({ service.postCompareRepo(body) }) { it }
    }
}