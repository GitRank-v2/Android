package com.dragonguard.android.ui.menu.org.approval

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.repository.menu.org.approval.ApproveOrgRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.RequestStatus
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproveOrgViewModel @Inject constructor(
    private val repository: ApproveOrgRepository
) : BaseViewModel<ApproveOrgContract.ApproveOrgEvent, ApproveOrgContract.ApproveOrgStates, ApproveOrgContract.ApproveOrgEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): ApproveOrgContract.ApproveOrgStates {
        pref = getPref()
        return ApproveOrgContract.ApproveOrgStates(
            LoadState.INIT,
            ApproveOrgContract.ApproveOrgState.RequestedOrg(emptyList()),
            ApproveOrgContract.ApproveOrgState.RequestedOrg(emptyList()),
            ApproveOrgContract.ApproveOrgState.Token(pref.getJwtToken("")),
            ApproveOrgContract.ApproveOrgState.ApproveOrg(false),
            ApproveOrgContract.ApproveOrgState.RejectOrg(false),
            ApproveOrgContract.ApproveOrgState.ApproveFinish(-1)
        )
    }

    override fun handleEvent(event: ApproveOrgContract.ApproveOrgEvent) {
        viewModelScope.launch {
            when (event) {
                is ApproveOrgContract.ApproveOrgEvent.GetRequestedOrg -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.statusOrgList(RequestStatus.REQUESTED.status, event.page).onSuccess {
                        Log.d("ApproveOrgViewModel", "GetRequestedOrg: $it")
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                receivedOrg = ApproveOrgContract.ApproveOrgState.RequestedOrg(it)
                            )
                        }
                    }.onFail {

                    }.onError {
                        Log.d("ApproveOrgViewModel", "GetRequestedOrg: ${it.message}")
                    }

                }

                is ApproveOrgContract.ApproveOrgEvent.ClickApprove -> {
                    setState { copy(approveOrg = ApproveOrgContract.ApproveOrgState.ApproveOrg(true)) }
                    repository.approveOrgRequest(
                        event.orgId,
                        OrgApprovalModel(
                            RequestStatus.ACCEPTED.status
                        )
                    ).onSuccess {
                        setState {
                            copy(
                                approveFinish = ApproveOrgContract.ApproveOrgState.ApproveFinish(
                                    event.position
                                )
                            )
                        }
                    }.onFail {

                    }

                }

                is ApproveOrgContract.ApproveOrgEvent.ClickReject -> {
                    setState { copy(rejectOrg = ApproveOrgContract.ApproveOrgState.RejectOrg(true)) }
                    repository.approveOrgRequest(
                        event.orgId,
                        OrgApprovalModel(
                            RequestStatus.DENIED.status
                        )
                    ).onSuccess {
                        setState {
                            copy(
                                approveFinish = ApproveOrgContract.ApproveOrgState.ApproveFinish(
                                    event.position
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is ApproveOrgContract.ApproveOrgEvent.ResetClick -> {
                    setState {
                        copy(
                            approveOrg = ApproveOrgContract.ApproveOrgState.ApproveOrg(false),
                            rejectOrg = ApproveOrgContract.ApproveOrgState.RejectOrg(false)
                        )
                    }
                }

                is ApproveOrgContract.ApproveOrgEvent.AddReceivedOrg -> {
                    setState {
                        copy(
                            loadState = LoadState.REFRESH,
                            requestedOrg = ApproveOrgContract.ApproveOrgState.RequestedOrg(
                                requestedOrg.org + receivedOrg.org
                            )
                        )
                    }
                }
            }
        }
    }

    fun getRequestedOrg(page: Int) {
        setEvent(ApproveOrgContract.ApproveOrgEvent.GetRequestedOrg(page))
    }

    fun clickApprove(orgId: Long, position: Int) {
        setEvent(ApproveOrgContract.ApproveOrgEvent.ClickApprove(orgId, position))
    }

    fun clickReject(orgId: Long, position: Int) {
        setEvent(ApproveOrgContract.ApproveOrgEvent.ClickReject(orgId, position))
    }

    fun resetClick() {
        setEvent(ApproveOrgContract.ApproveOrgEvent.ResetClick)
    }

    fun addReceivedOrg() {
        setEvent(ApproveOrgContract.ApproveOrgEvent.AddReceivedOrg)
    }
}