package com.dragonguard.android.ui.ranking

import com.dragonguard.android.data.model.rankings.OrgInternalRankingModelItem
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class OrganizationInternalContract {
    sealed class OrganizationInternalEvent : UiEvent {
        data class SearchOrgId(val orgName: String) : OrganizationInternalEvent()
        data class GetOrgInternalRankings(val orgId: Long, val page: Int) :
            OrganizationInternalEvent()
    }

    sealed class OrganizationInternalState {
        data class OrgId(val orgId: Long) : OrganizationInternalState()
        data class OrgInternalRankings(val orgInternalRankings: List<OrgInternalRankingModelItem>) :
            OrganizationInternalState()

        data class Token(val token: String) : OrganizationInternalState()
    }

    data class OrganizationInternalStates(
        val loadState: LoadState,
        val orgId: OrganizationInternalState.OrgId,
        val receivedRankings: OrganizationInternalState.OrgInternalRankings,
        val token: OrganizationInternalState.Token
    ) : UiState

    sealed class OrganizationInternalEffect : UiEffect {
        data class ShowToast(val message: String) : OrganizationInternalEffect()
    }
}