package com.dragonguard.android.data.repository.profile.other

import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.util.DataResult

interface OthersProfileRepository {
    suspend fun otherProfile(githubId: String): DataResult<UserProfileModel>
}