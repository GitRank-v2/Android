package com.dragonguard.android.data.repository.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class RepoCompareRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    RepoCompareRepository {
    override suspend fun postCompareRepoMembersRequest(
        first: String,
        second: String
    ): DataResult<CompareRepoMembersResponseModel> {
        val query = HashMap<String, String>()
        query.put("first", first)
        query.put("second", second)
        return handleApi({ service.postCompareRepoMembers(query) }, retrofit) { it.data }
    }

    override suspend fun postCompareRepoRequest(
        first: String,
        second: String
    ): DataResult<CompareRepoResponseModel> {
        val query = HashMap<String, String>()
        query.put("first", first)
        query.put("second", second)
        return handleApi({ service.postCompareRepo(query) }, retrofit) { it.data }
    }
}