package com.dragonguard.android.ui.menu

import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class MenuContract {
    sealed class MenuEvent : UiEvent {
        data object CheckAdmin : MenuEvent()
        data object WithDrawAccount : MenuEvent()
    }

    sealed class MenuState {
        data class AdminState(val isAdmin: Boolean) : MenuState()
        data class WithDrawState(val isSuccess: Boolean) : MenuState()
        data class Token(val token: String) : MenuState()
    }

    data class MenuStates(
        val admin: MenuState.AdminState,
        val withDraw: MenuState.WithDrawState,
        val token: MenuState.Token
    ) : UiState

    sealed class MenuEffect : UiEffect {

    }
}