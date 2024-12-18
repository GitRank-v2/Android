package com.dragonguard.android.ui.login

import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class LoginContract {
    sealed class LoginEvent : UiEvent {
        data class SetJwtToken(val token: String, val refreshToken: String) : LoginEvent()
        data object RefreshToken : LoginEvent()
        data object LogOut : LoginEvent()
    }

    sealed class LoginState {
    }

    data class LoginStates(
        val loginState: LoadState,
    ) : UiState

    sealed class LoginEffect : UiEffect {

    }
}