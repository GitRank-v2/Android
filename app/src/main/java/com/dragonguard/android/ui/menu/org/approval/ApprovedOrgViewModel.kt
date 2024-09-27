package com.dragonguard.android.ui.menu.org.approval

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.RequestStatus
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class ApprovedOrgViewModel :
    BaseViewModel<ApprovedOrgContract.ApprovedOrgEvent, ApprovedOrgContract.ApprovedOrgStates, ApprovedOrgContract.ApprovedOrgEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): ApprovedOrgContract.ApprovedOrgStates {
        pref = getPref()
        repository = getRepository()
        return ApprovedOrgContract.ApprovedOrgStates(
            state = ApprovedOrgContract.ApprovedOrgState.LoadState.Loading,
            approvedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(ApproveRequestOrgModel()),
            token = ApprovedOrgContract.ApprovedOrgState.Token("")
        )
    }

    override fun handleEvent(event: ApprovedOrgContract.ApprovedOrgEvent) {
        viewModelScope.launch {
            when (event) {
                is ApprovedOrgContract.ApprovedOrgEvent.GetApprovedOrg -> {
                    setState { copy(state = ApprovedOrgContract.ApprovedOrgState.LoadState.Loading) }
                    repository.statusOrgList(RequestStatus.ACCEPTED.status, event.page).onSuccess {
                        setState {
                            copy(
                                state = ApprovedOrgContract.ApprovedOrgState.LoadState.Success,
                                approvedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(it)
                            )
                        }
                    }.onFail {

                    }

                }
            }
        }
    }

    fun getApprovedOrg(page: Int) {
        setEvent(ApprovedOrgContract.ApprovedOrgEvent.GetApprovedOrg(page))
    }
}