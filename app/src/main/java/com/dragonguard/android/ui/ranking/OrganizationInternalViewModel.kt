package com.dragonguard.android.ui.ranking

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class OrganizationInternalViewModel :
    BaseViewModel<OrganizationInternalContract.OrganizationInternalEvent, OrganizationInternalContract.OrganizationInternalStates, OrganizationInternalContract.OrganizationInternalEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): OrganizationInternalContract.OrganizationInternalStates {
        pref = getPref()
        repository = getRepository()
        return OrganizationInternalContract.OrganizationInternalStates(
            LoadState.INIT,
            OrganizationInternalContract.OrganizationInternalState.OrgId(-1L),
            OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(
                OrgInternalRankingModel()
            ),
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
                    repository.searchOrgId(event.orgName).onSuccess {
                        setState {
                            copy(
                                orgId = OrganizationInternalContract.OrganizationInternalState.OrgId(
                                    it
                                )
                            )
                        }
                    }.onFail {

                    }

                }

                is OrganizationInternalContract.OrganizationInternalEvent.GetOrgInternalRankings -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.orgInternalRankings(event.orgId, event.page).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                receivedRankings = OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(
                                    it
                                )
                            )
                        }
                    }.onFail {

                    }

                }

                is OrganizationInternalContract.OrganizationInternalEvent.AddRanking -> {
                    setState {
                        copy(
                            orgInternalRankings = OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(
                                OrgInternalRankingModel(orgInternalRankings.orgInternalRankings.data + receivedRankings.orgInternalRankings.data)
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

    fun addRanking() {
        setEvent(OrganizationInternalContract.OrganizationInternalEvent.AddRanking)
    }
}