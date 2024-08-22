package com.dragonguard.android.ui.main

import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class MainActivityContract {
    sealed class MainActivityEvent : UiEvent {
        object GetUserInfo : MainActivityEvent()
        object SearchClick : MainActivityEvent()
        object GetNewToken : MainActivityEvent()
        object Logout : MainActivityEvent()
    }

    sealed class MainActivityState {
        sealed class LoadState : MainActivityState() {
            object Initial : LoadState()
            object Loading : LoadState()
            object Success : LoadState()
            object Error : LoadState()
        }

        data class UserInfo(val userInfo: UserInfoModel) : MainActivityState()
        data class SearchClicked(val clicked: Boolean) : MainActivityState()
        data class NewAccessToken(val token: String?) : MainActivityState()
        data class NewRefreshToken(val refreshToken: String?) : MainActivityState()
    }

    data class MainActivityStates(
        val loadState: MainActivityState.LoadState,
        val userInfo: MainActivityState.UserInfo,
        val searchClicked: MainActivityState.SearchClicked,
        val newAccessToken: MainActivityState.NewAccessToken,
        val newRefreshToken: MainActivityState.NewRefreshToken
    ) : UiState

    sealed class MainActivityEffect : UiEffect {

    }
}