package com.dragonguard.android.ui.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class ApprovedOrgContract {
    sealed class ApprovedOrgEvent : UiEvent {
        data class GetApprovedOrg(val page: Int) : ApprovedOrgEvent()
    }

    sealed class ApprovedOrgState {
        sealed class LoadState : ApprovedOrgState() {
            data object Loading : LoadState()
            data object Success : LoadState()
            data object Error : LoadState()
        }

        data class ApprovedOrg(val approvedOrg: ApproveRequestOrgModel) : ApprovedOrgState()
        data class Token(val token: String) : ApprovedOrgState()
    }

    data class ApprovedOrgStates(
        val state: ApprovedOrgState = ApprovedOrgState.LoadState.Loading,
        val approvedOrg: ApprovedOrgState.ApprovedOrg,
        val token: ApprovedOrgState.Token
    ) : UiState

    sealed class ApprovedOrgEffect : UiEffect {

    }
}