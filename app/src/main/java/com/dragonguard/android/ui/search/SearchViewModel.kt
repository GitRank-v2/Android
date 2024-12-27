package com.dragonguard.android.ui.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.search.SearchRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onException
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : BaseViewModel<SearchContract.SearchEvent, SearchContract.SearchStates, SearchContract.SearchEffect>() {
    override fun createInitialState(): SearchContract.SearchStates {
        return SearchContract.SearchStates(
            LoadState.INIT,
            SearchContract.SearchState.UserNames(arrayListOf()),
            SearchContract.SearchState.RepoNames(arrayListOf())
        )
    }

    override fun handleEvent(event: SearchContract.SearchEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchContract.SearchEvent.GetUserNames -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    Log.d("SearchViewModel", "handleEvent: ${event.name}, ${event.count}")
                    repository.getUserNames(event.name, event.count).onSuccess {
                        Log.d("SearchViewModel", "handleEvent success: $it")
                        currentState.userNames.userNames.addAll(it)
                        setState { copy(searchState = LoadState.USER_SUCCESS) }
                    }.onFail {
                        Log.d("SearchViewModel", "handleEvent fail: $it")
                    }.onError {
                        Log.d("SearchViewModel", "handleEvent error: $it")
                    }.onException {
                        Log.d("SearchViewModel", "handleEvent exception: ${it.message}")
                    }
                }

                is SearchContract.SearchEvent.GetRepositoryNamesNoFilters -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    repository.getRepositoryNames(event.name, event.count).onSuccess {
                        Log.d("SearchViewModel", "handleEvent success: $it")
                        currentState.repoNames.repoNames.addAll(it)
                        setState { copy(searchState = LoadState.REPO_SUCCESS) }
                    }.onFail {
                        Log.d("SearchViewModel", "handleEvent fail: $it")
                    }.onError {
                        Log.d("SearchViewModel", "handleEvent error: $it")
                    }.onException {
                        Log.d("SearchViewModel", "handleEvent exception: ${it.message}")
                    }

                }

                is SearchContract.SearchEvent.GetRepositoryNamesWithFilters -> {
                    setState { copy(searchState = LoadState.LOADING) }
                    repository.getRepositoryNamesWithFilters(
                        event.name,
                        event.count,
                        event.filters,
                    ).onSuccess {
                        currentState.repoNames.repoNames.addAll(it)
                        setState { copy(searchState = LoadState.REPO_SUCCESS) }
                    }.onFail {
                        Log.d("SearchViewModel", "handleEvent fail: $it")
                    }.onError {
                        Log.d("SearchViewModel", "handleEvent error: $it")
                    }.onException {
                        Log.d("SearchViewModel", "handleEvent exception: ${it.message}")
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
            }
        }
    }

    fun searchUserNames(name: String, count: Int) {
        setEvent(SearchContract.SearchEvent.GetUserNames(name, count))
    }

    fun searchRepositoryNamesNoFilters(name: String, count: Int) {
        setEvent(SearchContract.SearchEvent.GetRepositoryNamesNoFilters(name, count))
    }

    fun searchRepositoryNamesWithFilters(name: String, count: Int, filters: String) {
        setEvent(SearchContract.SearchEvent.GetRepositoryNamesWithFilters(name, count, filters))
    }

    fun clearRepoNames() {
        setEvent(SearchContract.SearchEvent.ClearRepoNames)
    }

    fun clearUserNames() {
        setEvent(SearchContract.SearchEvent.ClearUserNames)
    }
}