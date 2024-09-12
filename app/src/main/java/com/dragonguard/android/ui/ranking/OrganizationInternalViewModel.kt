package com.dragonguard.android.ui.ranking

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class OrganizationInternalViewModel :
    BaseViewModel<OrganizationInternalContract.OrganizationInternalEvent, OrganizationInternalContract.OrganizationInternalStates, OrganizationInternalContract.OrganizationInternalEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): OrganizationInternalContract.OrganizationInternalStates {
        pref = getPref()
        repository = getRepository()
        return OrganizationInternalContract.OrganizationInternalStates(
            OrganizationInternalContract.OrganizationInternalState.LoadState.Loading,
            OrganizationInternalContract.OrganizationInternalState.OrgId(-1L),
            OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(
                OrgInternalRankingModel()
            ),
            OrganizationInternalContract.OrganizationInternalState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: OrganizationInternalContract.OrganizationInternalEvent) {
        viewModelScope.launch {
            when (event) {
                is OrganizationInternalContract.OrganizationInternalEvent.SearchOrgId -> {
                    val result = repository.searchOrgId(event.orgName, currentState.token.token)
                    setState {
                        copy(
                            orgId = OrganizationInternalContract.OrganizationInternalState.OrgId(
                                result
                            )
                        )
                    }
                }

                is OrganizationInternalContract.OrganizationInternalEvent.GetOrgInternalRankings -> {
                    setState { copy(loadState = OrganizationInternalContract.OrganizationInternalState.LoadState.Loading) }
                    val result = repository.orgInternalRankings(
                        event.orgId,
                        event.page,
                        currentState.token.token
                    )
                    setState {
                        copy(
                            loadState = OrganizationInternalContract.OrganizationInternalState.LoadState.Success,
                            orgInternalRankings = OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(
                                result
                            )
                        )
                    }
                }
            }
        }
    }

    fun searchOrgId(orgName: String) {
        setEvent(OrganizationInternalContract.OrganizationInternalEvent.SearchOrgId(orgName))
    }

    fun getOrgInternalRankings(orgId: Long, page: Int) {
        setEvent(
            OrganizationInternalContract.OrganizationInternalEvent.GetOrgInternalRankings(
                orgId,
                page
            )
        )
    }
}