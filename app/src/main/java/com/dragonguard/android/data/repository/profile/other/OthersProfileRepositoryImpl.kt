package com.dragonguard.android.data.repository.profile.other

import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleApi
import javax.inject.Inject

class OthersProfileRepositoryImpl @Inject constructor(private val service: GitRankService) :
    OthersProfileRepository {
    override suspend fun otherProfile(githubId: String): DataResult<UserProfileModel> {
        return handleApi({ service.getOthersProfile(githubId) }) { it }
    }
}