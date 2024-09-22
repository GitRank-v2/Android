package com.dragonguard.android.ui.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class SearchOrganizationContract {
    sealed class SearchOrganizationEvent : UiEvent {
        data class SearchOrgNames(val name: String, val type: String, val count: Int) :
            SearchOrganizationEvent()
    }

    sealed class SearchOrganizationState {
        sealed class LoadState : SearchOrganizationState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object Success : LoadState()
            data object Error : LoadState()
        }


        data class OrgNames(val names: OrganizationNamesModel) : SearchOrganizationState()
        data class Token(val token: String) : SearchOrganizationState()
    }

    data class SearchOrganizationStates(
        val state: SearchOrganizationState.LoadState,
        val orgNames: SearchOrganizationState.OrgNames,
        val token: SearchOrganizationState.Token
    ) : UiState

    sealed class SearchOrganizationEffect : UiEffect {

    }

}