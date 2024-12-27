package com.dragonguard.android.ui.menu.org.approval

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.menu.org.approval.ApprovedOrgRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.RequestStatus
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApprovedOrgViewModel @Inject constructor(
    private val repository: ApprovedOrgRepository
) : BaseViewModel<ApprovedOrgContract.ApprovedOrgEvent, ApprovedOrgContract.ApprovedOrgStates, ApprovedOrgContract.ApprovedOrgEffect>() {
    override fun createInitialState(): ApprovedOrgContract.ApprovedOrgStates {

        return ApprovedOrgContract.ApprovedOrgStates(
            state = LoadState.INIT,
            approvedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(emptyList()),
            receivedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(emptyList()),
            token = ApprovedOrgContract.ApprovedOrgState.Token("")
        )
    }

    override fun handleEvent(event: ApprovedOrgContract.ApprovedOrgEvent) {
        viewModelScope.launch {
            when (event) {
                is ApprovedOrgContract.ApprovedOrgEvent.GetApprovedOrg -> {
                    setState { copy(state = LoadState.LOADING) }
                    repository.statusOrgList(RequestStatus.ACCEPTED.status, event.page).onSuccess {
                        Log.d("ApprovedOrgViewModel", it.toString())
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                receivedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(it)
                            )
                        }
                    }.onFail {

                    }.onError {
                        Log.d("ApprovedOrgViewModel", it.message.toString())
                    }

                }

                is ApprovedOrgContract.ApprovedOrgEvent.AddReceivedOrg -> {
                    setState {
                        copy(
                            state = LoadState.REFRESH,
                            approvedOrg = ApprovedOrgContract.ApprovedOrgState.ApprovedOrg(
                                approvedOrg.approvedOrg + receivedOrg.approvedOrg
                            )
                        )
                    }
                }
            }
        }
    }

    fun getApprovedOrg(page: Int) {
        setEvent(ApprovedOrgContract.ApprovedOrgEvent.GetApprovedOrg(page))
    }

    fun addReceivedOrg() {
        setEvent(ApprovedOrgContract.ApprovedOrgEvent.AddReceivedOrg)
    }
}