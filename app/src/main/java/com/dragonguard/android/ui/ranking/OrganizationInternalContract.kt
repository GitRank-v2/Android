package com.dragonguard.android.ui.ranking

import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class OrganizationInternalContract {
    sealed class OrganizationInternalEvent : UiEvent {
        data class SearchOrgId(val orgName: String) : OrganizationInternalEvent()
        data class GetOrgInternalRankings(val orgId: Long, val page: Int) :
            OrganizationInternalEvent()
    }

    sealed class OrganizationInternalState {
        sealed class LoadState : OrganizationInternalState() {
            data object Loading : LoadState()
            data object Success : LoadState()
            data class Error(val message: String) : LoadState()
        }

        data class OrgId(val orgId: Long) : OrganizationInternalState()
        data class OrgInternalRankings(val orgInternalRankings: OrgInternalRankingModel) :
            OrganizationInternalState()

        data class Token(val token: String) : OrganizationInternalState()
    }

    data class OrganizationInternalStates(
        val loadState: OrganizationInternalState.LoadState,
        val orgId: OrganizationInternalState.OrgId,
        val orgInternalRankings: OrganizationInternalState.OrgInternalRankings,
        val token: OrganizationInternalState.Token
    ) : UiState

    sealed class OrganizationInternalEffect : UiEffect {
        data class ShowToast(val message: String) : OrganizationInternalEffect()
    }
}