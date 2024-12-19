package com.dragonguard.android.ui.menu.org.search

import com.dragonguard.android.data.model.org.OrganizationNamesModelItem
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class SearchOrganizationContract {
    sealed class SearchOrganizationEvent : UiEvent {
        data class SearchOrgNames(val name: String, val type: String, val count: Int) :
            SearchOrganizationEvent()

        data class SearchOrgWithNoName(val type: String, val count: Int) : SearchOrganizationEvent()
        data object AddReceivedOrgNames : SearchOrganizationEvent()
    }

    sealed class SearchOrganizationState {
        data class OrgNames(val names: List<OrganizationNamesModelItem>) : SearchOrganizationState()
    }

    data class SearchOrganizationStates(
        val state: LoadState,
        val orgNames: SearchOrganizationState.OrgNames,
        val receivedOrgNames: SearchOrganizationState.OrgNames,
    ) : UiState

    sealed class SearchOrganizationEffect : UiEffect {

    }

}