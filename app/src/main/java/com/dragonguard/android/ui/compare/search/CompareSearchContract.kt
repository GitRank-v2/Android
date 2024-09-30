package com.dragonguard.android.ui.compare.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class CompareSearchContract {
    sealed class CompareSearchEvent : UiEvent {
        data class SearchRepo(val name: String, val count: Int) : CompareSearchEvent()
        data object AddReceivedRepo : CompareSearchEvent()
    }

    sealed class CompareSearchState {
        data class Token(val token: String) : CompareSearchState()
        data class SearchResults(val searchResults: List<RepoSearchResultModel>) :
            CompareSearchState()
    }

    data class CompareSearchStates(
        val loadState: LoadState,
        val token: CompareSearchState.Token,
        val searchResults: CompareSearchState.SearchResults,
        val receivedSearchResult: CompareSearchState.SearchResults
    ) : UiState

    sealed class CompareSearchEffect : UiEffect {

    }
}