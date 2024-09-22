package com.dragonguard.android.ui.login

import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class LoginContract {
    sealed class LoginEvent : UiEvent {
        data class SetJwtToken(val token: String, val refreshToken: String) : LoginEvent()
        data object GetJwtToken : LoginEvent()
    }

    sealed class LoginState {
        data class LoginStat(val login: Boolean?) : LoginState()
        data class Token(val token: String) : LoginState()
        data class RefreshToken(val refreshToken: String) : LoginState()
        data class Key(val key: String) : LoginState()
    }

    data class LoginStates(
        val loginState: LoginState.LoginStat,
        val token: LoginState.Token,
        val refreshToken: LoginState.RefreshToken,
        val key: LoginState.Key
    ) : UiState

    sealed class LoginEffect : UiEffect {

    }
}