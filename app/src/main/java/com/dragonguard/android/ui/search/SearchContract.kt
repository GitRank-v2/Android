package com.dragonguard.android.ui.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class SearchContract {
    sealed class SearchEvent : UiEvent {
        data class GetUserNames(val name: String, val count: Int) : SearchEvent()
        data class GetRepositoryNamesNoFilters(val name: String, val count: Int) : SearchEvent()

        data class GetRepositoryNamesWithFilters(
            val name: String,
            val count: Int,
            val filters: String,
        ) : SearchEvent()

        data object ClearUserNames : SearchEvent()
        data object ClearRepoNames : SearchEvent()
    }

    sealed class SearchState {
        data class RepoNames(val repoNames: ArrayList<RepoSearchResultModel>) : SearchState()
        data class UserNames(val userNames: ArrayList<UserNameModelItem>) : SearchState()
    }

    data class SearchStates(
        val searchState: LoadState,
        val userNames: SearchState.UserNames,
        val repoNames: SearchState.RepoNames,
    ) : UiState

    sealed class SearchEffect : UiEffect {

    }
}