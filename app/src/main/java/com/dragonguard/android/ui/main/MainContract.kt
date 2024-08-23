package com.dragonguard.android.ui.main

import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class MainContract {
    sealed class MainEvent : UiEvent {
        object GetUserInfo : MainEvent()
        object ClickSearch : MainEvent()
        object ClickUserIcon : MainEvent()
        object GetNewToken : MainEvent()
        object Logout : MainEvent()
        data class SetRepeat(val repeat: Boolean) : MainEvent()
    }

    sealed class MainState {
        sealed class LoadState : MainState() {
            object Initial : LoadState()
            object Loading : LoadState()
            object Success : LoadState()
            object Error : LoadState()
        }

        data class UserInfo(val userInfo: UserInfoModel) : MainState()
        data class ClickSearch(val clicked: Boolean) : MainState()
        data class ClickUserIcon(val clicked: Boolean) : MainState()
        data class NewAccessToken(val token: String?) : MainState()
        data class NewRefreshToken(val refreshToken: String?) : MainState()
        data class RepeatState(val repeat: Boolean) : MainState()
    }

    data class MainStates(
        val loadState: MainState.LoadState,
        val userInfo: MainState.UserInfo,
        val clickSearch: MainState.ClickSearch,
        val clickUserIcon: MainState.ClickUserIcon,
        val newAccessToken: MainState.NewAccessToken,
        val newRefreshToken: MainState.NewRefreshToken,
        val repeatState: MainState.RepeatState
    ) : UiState

    sealed class MainActivityEffect : UiEffect {

    }
}