package com.dragonguard.android.ui.main

import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class MainContract {
    sealed class MainEvent : UiEvent {
        data object GetUserInfo : MainEvent()
        data object ClickSearch : MainEvent()
        data object ClickUserIcon : MainEvent()
        data object GetNewToken : MainEvent()
        data object Logout : MainEvent()
        data class SetRepeat(val repeat: Boolean) : MainEvent()
        data object SetFinish : MainEvent()
    }

    sealed class MainState {
        data class UserInfo(val userInfo: UserInfoModel) : MainState()
        data class ClickSearch(val clicked: Boolean) : MainState()
        data class ClickUserIcon(val clicked: Boolean) : MainState()
        data class NewAccessToken(val token: String?) : MainState()
        data class NewRefreshToken(val refreshToken: String?) : MainState()
        data class RepeatState(val repeat: Boolean) : MainState()
    }

    data class MainStates(
        val loadState: LoadState,
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