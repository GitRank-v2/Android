package com.dragonguard.android.ui.compare.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class CompareSearchContract {
    sealed class CompareSearchEvent : UiEvent {
        data class SearchRepo(val name: String, val count: Int) : CompareSearchEvent()
    }

    sealed class CompareSearchState {
        sealed class LoadState : CompareSearchState() {
            data object Loading : LoadState()
            data object Success : LoadState()
            data class Error(val message: String) : LoadState()
        }

        data class Token(val token: String) : CompareSearchState()
        data class SearchResults(val searchResults: List<RepoSearchResultModel>) :
            CompareSearchState()
    }

    data class CompareSearchStates(
        val loadState: CompareSearchState.LoadState,
        val token: CompareSearchState.Token,
        val searchResults: CompareSearchState.SearchResults
    ) : UiState

    sealed class CompareSearchEffect : UiEffect {

    }
}