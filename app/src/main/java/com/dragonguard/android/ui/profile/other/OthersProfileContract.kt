package com.dragonguard.android.ui.profile.other

import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class OthersProfileContract {
    sealed class UserProfileEvent : UiEvent {
        data class GetOthersProfile(val name: String) : UserProfileEvent()
        data object GetUserProfile : UserProfileEvent()
    }

    sealed class UserProfileState {
        data class Token(val token: String) : UserProfileState()
        data class UserProfile(val userProfile: UserProfileModel) : UserProfileState()
    }

    data class UserProfileStates(
        val loadState: LoadState,
        val token: UserProfileState.Token,
        val userProfile: UserProfileState.UserProfile
    ) : UiState

    sealed class UserProfileEffect : UiEffect {
    }
}