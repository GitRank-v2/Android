package com.dragonguard.android.ui.menu.org.approval

import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.RequestStatus

class ApproveOrgViewModel :
    BaseViewModel<ApproveOrgContract.ApproveOrgEvent, ApproveOrgContract.ApproveOrgStates, ApproveOrgContract.ApproveOrgEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): ApproveOrgContract.ApproveOrgStates {
        pref = getPref()
        repository = getRepository()
        return ApproveOrgContract.ApproveOrgStates(
            ApproveOrgContract.ApproveOrgState.LoadState.Loading,
            ApproveOrgContract.ApproveOrgState.RequestedOrg(ApproveRequestOrgModel()),
            ApproveOrgContract.ApproveOrgState.Token(pref.getJwtToken("")),
            ApproveOrgContract.ApproveOrgState.ApproveOrg(false),
            ApproveOrgContract.ApproveOrgState.RejectOrg(false),
            ApproveOrgContract.ApproveOrgState.ApproveFinish(-1)
        )
    }

    override fun handleEvent(event: ApproveOrgContract.ApproveOrgEvent) {
        when (event) {
            is ApproveOrgContract.ApproveOrgEvent.GetRequestedOrg -> {
                setState { copy(loadState = ApproveOrgContract.ApproveOrgState.LoadState.Loading) }
                val result = repository.statusOrgList(RequestStatus.REQUESTED.status, event.page)
                setState {
                    copy(
                        loadState = ApproveOrgContract.ApproveOrgState.LoadState.Success,
                        requestedOrg = ApproveOrgContract.ApproveOrgState.RequestedOrg(result)
                    )
                }
            }

            is ApproveOrgContract.ApproveOrgEvent.ClickApprove -> {
                setState { copy(approveOrg = ApproveOrgContract.ApproveOrgState.ApproveOrg(true)) }
                val result = repository.approveOrgRequest(
                    OrgApprovalModel(
                        RequestStatus.ACCEPTED.status,
                        event.orgId
                    )
                )
                setState {
                    copy(
                        approveFinish = ApproveOrgContract.ApproveOrgState.ApproveFinish(
                            event.position
                        )
                    )
                }
            }

            is ApproveOrgContract.ApproveOrgEvent.ClickReject -> {
                setState { copy(rejectOrg = ApproveOrgContract.ApproveOrgState.RejectOrg(true)) }
                val result = repository.approveOrgRequest(
                    OrgApprovalModel(RequestStatus.DENIED.status, event.orgId)
                )
                setState {
                    copy(
                        approveFinish = ApproveOrgContract.ApproveOrgState.ApproveFinish(
                            event.position
                        )
                    )
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

}