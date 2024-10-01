package com.dragonguard.android.ui.search

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class SearchViewModel :
    BaseViewModel<SearchContract.SearchEvent, SearchContract.SearchStates, SearchContract.SearchEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): SearchContract.SearchStates {
        pref = getPref()
        repository = getRepository()
        return SearchContract.SearchStates(
            LoadState.INIT,
            SearchContract.SearchState.UserNames(arrayListOf()),
            SearchContract.SearchState.UserNames(arrayListOf()),
            SearchContract.SearchState.RepoNames(arrayListOf()),
            SearchContract.SearchState.RepoNames(arrayListOf())
        )
    }

    override fun handleEvent(event: SearchContract.SearchEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchContract.SearchEvent.GetUserNames -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    repository.getUserNames(event.name, event.count, event.type).onSuccess {
                        setState {
                            copy(
                                searchState = LoadState.USERSUCCESS,
                                receivedUserNames = SearchContract.SearchState.UserNames(it)
                            )
                        }
                    }.onFail {

                    }
                }

                is SearchContract.SearchEvent.GetRepositoryNamesNoFilters -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    repository.getRepositoryNames(event.name, event.count, event.type).onSuccess {
                        setState {
                            copy(
                                searchState = LoadState.REPOSUCCESS,
                                receivedRepoNames = SearchContract.SearchState.RepoNames(it)
                            )
                        }
                    }.onFail {

                    }

                }

                is SearchContract.SearchEvent.GetRepositoryNamesWithFilters -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    repository.getRepositoryNamesWithFilters(
                        event.name,
                        event.count,
                        event.filters,
                        event.type,
                    ).onSuccess {
                        setState {
                            copy(
                                searchState = LoadState.REPOSUCCESS,
                                receivedRepoNames = SearchContract.SearchState.RepoNames(it)
                            )
                        }
                    }.onFail {

                    }
                }

                is SearchContract.SearchEvent.ClearRepoNames -> {
                    setState {
                        copy(
                            searchState = LoadState.INIT,
                            repoNames = SearchContract.SearchState.RepoNames(arrayListOf())
                        )
                    }
                }

                is SearchContract.SearchEvent.ClearUserNames -> {
                    setState {
                        copy(
                            searchState = LoadState.INIT,
                            userNames = SearchContract.SearchState.UserNames(arrayListOf())
                        )
                    }
                }

                is SearchContract.SearchEvent.AddReceivedUserNames -> {
                    setState {
                        copy(
                            userNames = SearchContract.SearchState.UserNames(
                                (userNames.userNames + receivedUserNames.userNames) as ArrayList
                            ),
                            receivedUserNames = SearchContract.SearchState.UserNames(arrayListOf())
                        )
                    }
                }

                is SearchContract.SearchEvent.AddReceivedRepoNames -> {
                    setState {
                        copy(
                            repoNames = SearchContract.SearchState.RepoNames(
                                (repoNames.repoNames + receivedRepoNames.repoNames) as ArrayList
                            ),
                            receivedRepoNames = SearchContract.SearchState.RepoNames(arrayListOf())
                        )
                    }
                }
            }
        }
    }

    fun searchUserNames(name: String, count: Int, type: String) {
        setEvent(SearchContract.SearchEvent.GetUserNames(name, count, type))
    }

    fun searchRepositoryNamesNoFilters(name: String, count: Int, type: String) {
        setEvent(SearchContract.SearchEvent.GetRepositoryNamesNoFilters(name, count, type))
    }

    fun searchRepositoryNamesWithFilters(name: String, count: Int, filters: String, type: String) {
        setEvent(
            SearchContract.SearchEvent.GetRepositoryNamesWithFilters(
                name,
                count,
                filters,
                type
            )
        )
    }

    fun clearRepoNames() {
        setEvent(SearchContract.SearchEvent.ClearRepoNames)
    }

    fun clearUserNames() {
        setEvent(SearchContract.SearchEvent.ClearUserNames)
    }

    fun addReceivedUserNames() {
        setEvent(SearchContract.SearchEvent.AddReceivedUserNames)
    }

    fun addReceivedRepoNames() {
        setEvent(SearchContract.SearchEvent.AddReceivedRepoNames)
    }
}