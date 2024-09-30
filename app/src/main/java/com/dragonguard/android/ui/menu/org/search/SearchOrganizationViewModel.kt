package com.dragonguard.android.ui.menu.org.search

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class SearchOrganizationViewModel :
    BaseViewModel<SearchOrganizationContract.SearchOrganizationEvent, SearchOrganizationContract.SearchOrganizationStates, SearchOrganizationContract.SearchOrganizationEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository

    override fun createInitialState(): SearchOrganizationContract.SearchOrganizationStates {
        pref = getPref()
        repository = getRepository()
        return SearchOrganizationContract.SearchOrganizationStates(
            LoadState.INIT,
            SearchOrganizationContract.SearchOrganizationState.OrgNames(OrganizationNamesModel()),
            SearchOrganizationContract.SearchOrganizationState.OrgNames(OrganizationNamesModel()),
            SearchOrganizationContract.SearchOrganizationState.Token("")
        )
    }

    override fun handleEvent(event: SearchOrganizationContract.SearchOrganizationEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchOrganizationContract.SearchOrganizationEvent.SearchOrgNames -> {
                    setState { copy(state = LoadState.LOADING) }
                    repository.getOrgNames(event.name, event.count, event.type).onSuccess {
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                receivedOrgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames(
                                    it
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is SearchOrganizationContract.SearchOrganizationEvent.AddReceivedOrgNames -> {
                    setState {
                        copy(
                            orgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames((orgNames.names + receivedOrgNames.names) as OrganizationNamesModel)

                        )
                    }
                }
            }
        }
    }

    fun searchOrgNames(name: String, type: String, count: Int) {
        setEvent(
            SearchOrganizationContract.SearchOrganizationEvent.SearchOrgNames(
                name,
                type,
                count
            )
        )
    }

    fun addReceivedOrgNames() {
        setEvent(SearchOrganizationContract.SearchOrganizationEvent.AddReceivedOrgNames)
    }
}