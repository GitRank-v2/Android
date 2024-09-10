package com.dragonguard.android.ui.profile.other

import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class OthersProfileContract {
    sealed class UserProfileEvent : UiEvent {
        data class GetOthersProfile(val name: String) : UserProfileEvent()
    }

    sealed class UserProfileState {
        sealed class LoadState : UserProfileState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object Success : LoadState()
            data object Error : LoadState()
        }

        data class Token(val token: String) : UserProfileState()
        data class UserProfile(val userProfile: UserProfileModel) : UserProfileState()
    }

    data class UserProfileStates(
        val loadState: UserProfileState.LoadState,
        val token: UserProfileState.Token,
        val userProfile: UserProfileState.UserProfile
    ) : UiState

    sealed class UserProfileEffect : UiEffect {
    }
}