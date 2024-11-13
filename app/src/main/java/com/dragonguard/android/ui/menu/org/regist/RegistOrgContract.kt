package com.dragonguard.android.ui.menu.org.regist

import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class RegistOrgContract {
    sealed class RegistOrgEvent : UiEvent {
        data class RequestRegistOrg(
            val orgName: String,
            val orgType: String,
            val orgEndPoint: String
        ) : RegistOrgEvent()
    }

    sealed class RegistOrgState {
        data class RegistResult(val result: RegistOrgResultModel) : RegistOrgState()
        data class Token(val token: String) : RegistOrgState()
    }

    data class RegistOrgStates(
        val state: LoadState,
        val token: RegistOrgState.Token,
        val registResult: RegistOrgState.RegistResult
    ) : UiState

    sealed class RegistOrgEffect : UiEffect {

    }
}