package com.dragonguard.android.ui.profile.user

import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class ClientProfileContract {
    sealed class ClientProfileEvent : UiEvent {
        data object GetClientDetail : ClientProfileEvent()
    }

    sealed class ClientProfileState {
        data class ClientDetail(val clientDetail: ClientDetailModel) : ClientProfileState()
        data class Token(val token: String) : ClientProfileState()
    }

    data class ClientProfileStates(
        val loadState: LoadState,
        val clientDetail: ClientProfileState.ClientDetail,
        val token: ClientProfileState.Token
    ) : UiState

    sealed class ClientProfileEffect : UiEffect {

    }
}