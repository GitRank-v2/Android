package com.dragonguard.android.ui.menu.org.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.menu.org.search.SearchOrganizationRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchOrganizationViewModel @Inject constructor(
    private val repository: SearchOrganizationRepository
) : BaseViewModel<SearchOrganizationContract.SearchOrganizationEvent, SearchOrganizationContract.SearchOrganizationStates, SearchOrganizationContract.SearchOrganizationEffect>() {
    override fun createInitialState(): SearchOrganizationContract.SearchOrganizationStates {
        return SearchOrganizationContract.SearchOrganizationStates(
            LoadState.INIT,
            SearchOrganizationContract.SearchOrganizationState.OrgNames(emptyList()),
            SearchOrganizationContract.SearchOrganizationState.OrgNames(emptyList()),
        )
    }

    override fun handleEvent(event: SearchOrganizationContract.SearchOrganizationEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchOrganizationContract.SearchOrganizationEvent.SearchOrgNames -> {
                    Log.d(
                        "SearchOrganizationViewModel",
                        "handleEvent: ${event.name} ${event.count} ${event.type}"
                    )
                    setState { copy(state = LoadState.LOADING) }
                    repository.getOrgNames(event.name, event.count, event.type).onSuccess {
                        Log.d("SearchOrganizationViewModel", "handleEvent: $it")
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                receivedOrgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames(
                                    it
                                )
                            )
                        }
                    }.onFail {
                        Log.d("SearchOrganizationViewModel", "handleEvent: $it")
                    }.onError {
                        Log.d("SearchOrganizationViewModel", "handleEvent: ${it.message}")
                    }
                }

                is SearchOrganizationContract.SearchOrganizationEvent.SearchOrgWithNoName -> {
                    Log.d(
                        "SearchOrganizationViewModel",
                        "handleEvent: ${event.count} ${event.type}"
                    )
                    setState { copy(state = LoadState.LOADING) }
                    repository.getOrgNames(event.count, event.type).onSuccess {
                        Log.d("SearchOrganizationViewModel", "handleEvent: $it")
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                receivedOrgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames(
                                    it
                                )
                            )
                        }
                    }.onFail {
                        Log.d("SearchOrganizationViewModel", "handleEvent: $it")
                    }.onError {
                        Log.d("SearchOrganizationViewModel", "handleEvent: ${it.message}")
                    }
                }

                is SearchOrganizationContract.SearchOrganizationEvent.AddReceivedOrgNames -> {
                    setState {
                        copy(
                            state = LoadState.REFRESH,
                            orgNames = SearchOrganizationContract.SearchOrganizationState.OrgNames(
                                orgNames.names + receivedOrgNames.names
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

    fun searchOrgNames(type: String, count: Int) {
        setEvent(
            SearchOrganizationContract.SearchOrganizationEvent.SearchOrgWithNoName(
                type,
                count
            )
        )
    }

    fun addReceivedOrgNames() {
        setEvent(SearchOrganizationContract.SearchOrganizationEvent.AddReceivedOrgNames)
    }
}