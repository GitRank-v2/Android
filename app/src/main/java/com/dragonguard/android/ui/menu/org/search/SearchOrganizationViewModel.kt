package com.dragonguard.android.ui.menu.org.search

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class SearchOrganizationViewModel :
    BaseViewModel<SearchOrganizationContract.SearchOrganizationEvent, SearchOrganizationContract.SearchOrganizationStates, SearchOrganizationContract.SearchOrganizationEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository

    override fun createInitialState(): SearchOrganizationContract.SearchOrganizationStates {
        pref = getPref()
        repository = getRepository()
        return SearchOrganizationContract.SearchOrganizationStates(
            SearchOrganizationContract.SearchOrganizationState.LoadState.Initial,
            SearchOrganizationContract.SearchOrganizationState.OrgNames(OrganizationNamesModel()),
            SearchOrganizationContract.SearchOrganizationState.Token("")
        )
    }

    override fun handleEvent(event: SearchOrganizationContract.SearchOrganizationEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchOrganizationContract.SearchOrganizationEvent.SearchOrgNames -> {
                    setState { copy(state = SearchOrganizationContract.SearchOrganizationState.LoadState.Loading) }
                    val result = repository.getOrgNames(event.name, event.count, event.type)
                    setState {
                        copy(
                            state = SearchOrganizationContract.SearchOrganizationState.LoadState.Success,
                            orgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames(
                                result
                            )
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
}