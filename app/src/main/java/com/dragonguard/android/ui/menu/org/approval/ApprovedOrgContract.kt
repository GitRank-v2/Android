package com.dragonguard.android.ui.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class ApprovedOrgContract {
    sealed class ApprovedOrgEvent : UiEvent {
        data class GetApprovedOrg(val page: Int) : ApprovedOrgEvent()
        data object AddReceivedOrg : ApprovedOrgEvent()
    }

    sealed class ApprovedOrgState {
        data class ApprovedOrg(val approvedOrg: ApproveRequestOrgModel) : ApprovedOrgState()
        data class Token(val token: String) : ApprovedOrgState()
    }

    data class ApprovedOrgStates(
        val state: LoadState,
        val approvedOrg: ApprovedOrgState.ApprovedOrg,
        val receivedOrg: ApprovedOrgState.ApprovedOrg,
        val token: ApprovedOrgState.Token
    ) : UiState

    sealed class ApprovedOrgEffect : UiEffect {

    }
}