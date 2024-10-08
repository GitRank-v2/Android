package com.dragonguard.android.ui.compare.search

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class CompareSearchViewModel :
    BaseViewModel<CompareSearchContract.CompareSearchEvent, CompareSearchContract.CompareSearchStates, CompareSearchContract.CompareSearchEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): CompareSearchContract.CompareSearchStates {
        pref = getPref()
        repository = getRepository()
        return CompareSearchContract.CompareSearchStates(
            CompareSearchContract.CompareSearchState.LoadState.Loading,
            CompareSearchContract.CompareSearchState.Token(pref.getJwtToken("")),
            CompareSearchContract.CompareSearchState.SearchResults(emptyList())
        )
    }

    override fun handleEvent(event: CompareSearchContract.CompareSearchEvent) {
        viewModelScope.launch {
            when (event) {
                is CompareSearchContract.CompareSearchEvent.SearchRepo -> {
                    setState { copy(loadState = CompareSearchContract.CompareSearchState.LoadState.Loading) }
                    val result = repository.getRepositoryNames(
                        event.name,
                        event.count,
                        REPOSITORIES,
                        pref.getJwtToken("")
                    )
                    setState {
                        copy(
                            loadState = CompareSearchContract.CompareSearchState.LoadState.Success,
                            searchResults = CompareSearchContract.CompareSearchState.SearchResults(
                                currentState.searchResults.searchResults + result
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

    companion object {
        private const val REPOSITORIES = "REPOSITORIES"
    }
}