package com.dragonguard.android.data.repository.profile.other

import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import retrofit2.Retrofit
import javax.inject.Inject

class OthersProfileRepositoryImpl @Inject constructor(
    private val service: GitRankService,
    private val retrofit: Retrofit
) :
    OthersProfileRepository {
    override suspend fun otherProfile(githubId: String): DataResult<UserProfileModel> {
        return handleApi({ service.getOthersProfile(githubId) }) { it.data }
    }

    override suspend fun getUserProfile(): DataResult<UserProfileModel> {
        return handleApi({ service.getUserProfile() }, retrofit) { it.data }
    }
}