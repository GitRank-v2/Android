package com.dragonguard.android.ui.compare.search

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareSearchViewModel @Inject constructor(
    private val pref: IdPreference,
    private val repository: ApiRepository
) : BaseViewModel<CompareSearchContract.CompareSearchEvent, CompareSearchContract.CompareSearchStates, CompareSearchContract.CompareSearchEffect>() {
    override fun createInitialState(): CompareSearchContract.CompareSearchStates {
        return CompareSearchContract.CompareSearchStates(
            LoadState.INIT,
            CompareSearchContract.CompareSearchState.Token(pref.getJwtToken("")),
            CompareSearchContract.CompareSearchState.SearchResults(arrayListOf()),
            CompareSearchContract.CompareSearchState.ReceivedSearchResult(emptyList())
        )
    }

    override fun handleEvent(event: CompareSearchContract.CompareSearchEvent) {
        viewModelScope.launch {
            when (event) {
                is CompareSearchContract.CompareSearchEvent.SearchRepo -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getRepositoryNames(event.name, event.count, REPOSITORIES).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                receivedSearchResult = CompareSearchContract.CompareSearchState.ReceivedSearchResult(
                                    it.data
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is CompareSearchContract.CompareSearchEvent.AddReceivedRepo -> {
                    currentState.searchResults.searchResults.addAll(currentState.receivedSearchResult.receivedSearchResult)
                    setState {
                        copy(
                            searchResults = CompareSearchContract.CompareSearchState.SearchResults(
                                currentState.searchResults.searchResults
                            )
                        )
                    }
                }
            }


        }
    }

    fun searchRepo(name: String, count: Int) {
        setEvent(CompareSearchContract.CompareSearchEvent.SearchRepo(name, count))
    }

    fun addReceivedRepo() {
        setEvent(CompareSearchContract.CompareSearchEvent.AddReceivedRepo)
    }

    companion object {
        private const val REPOSITORIES = "REPOSITORIES"
    }
}