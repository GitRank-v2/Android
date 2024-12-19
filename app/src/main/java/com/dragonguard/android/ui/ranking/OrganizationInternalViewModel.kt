package com.dragonguard.android.ui.ranking

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.repository.ranking.OrganizationInternalRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizationInternalViewModel @Inject constructor(
    private val repository: OrganizationInternalRepository
) : BaseViewModel<OrganizationInternalContract.OrganizationInternalEvent, OrganizationInternalContract.OrganizationInternalStates, OrganizationInternalContract.OrganizationInternalEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): OrganizationInternalContract.OrganizationInternalStates {
        pref = getPref()
        return OrganizationInternalContract.OrganizationInternalStates(
            LoadState.INIT,
            OrganizationInternalContract.OrganizationInternalState.OrgId(-1L),
            OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(emptyList()),
            OrganizationInternalContract.OrganizationInternalState.OrgInternalRankings(emptyList()),
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
                                orgInternalRankings.orgInternalRankings + receivedRankings.orgInternalRankings
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