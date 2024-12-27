package com.dragonguard.android.ui.profile.user

import com.dragonguard.android.data.model.detail.GitOrganization
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class ClientProfileContract {
    sealed class ClientProfileEvent : UiEvent {
        data object GetClientDetail : ClientProfileEvent()
    }

    sealed class ClientProfileState {
        data class ClientRepository(val clientRepository: List<String>) :
            ClientProfileState()

        data class ClientOrg(val clientOrg: List<GitOrganization>) : ClientProfileState()
    }

    data class ClientProfileStates(
        val loadState: LoadState,
        val clientRepository: ClientProfileState.ClientRepository,
        val clientOrg: ClientProfileState.ClientOrg
    ) : UiState

    sealed class ClientProfileEffect : UiEffect {

    }
}