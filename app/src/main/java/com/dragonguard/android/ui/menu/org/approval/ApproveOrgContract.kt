package com.dragonguard.android.ui.menu.org.approval

import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class ApproveOrgContract {
    sealed class ApproveOrgEvent : UiEvent {
        data class GetRequestedOrg(val page: Int) : ApproveOrgEvent()
        data class ClickApprove(val orgId: Long, val position: Int) : ApproveOrgEvent()
        data class ClickReject(val orgId: Long, val position: Int) : ApproveOrgEvent()
        data object ResetClick : ApproveOrgEvent()
    }

    sealed class ApproveOrgState {
        data class RequestedOrg(val org: ApproveRequestOrgModel) : ApproveOrgState()
        data class Token(val token: String) : ApproveOrgState()

        data class ApproveOrg(val status: Boolean) : ApproveOrgState()
        data class RejectOrg(val status: Boolean) : ApproveOrgState()
        data class ApproveFinish(val position: Int) : ApproveOrgState()
    }

    data class ApproveOrgStates(
        val loadState: LoadState,
        val requestedOrg: ApproveOrgState.RequestedOrg,
        val token: ApproveOrgState.Token,
        val approveOrg: ApproveOrgState.ApproveOrg,
        val rejectOrg: ApproveOrgState.RejectOrg,
        val approveFinish: ApproveOrgState.ApproveFinish
    ) : UiState

    sealed class ApproveOrgEffect : UiEffect {
        data class ShowToast(val message: String) : ApproveOrgEffect()
    }
}