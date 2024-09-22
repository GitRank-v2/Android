package com.dragonguard.android.ui.search

import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class SearchContract {
    sealed class SearchEvent : UiEvent {
        data class GetUserNames(val name: String, val count: Int, val type: String) : SearchEvent()
        data class GetRepositoryNamesNoFilters(val name: String, val count: Int, val type: String) :
            SearchEvent()

        data class GetRepositoryNamesWithFilters(
            val name: String,
            val count: Int,
            val filters: String,
            val type: String
        ) : SearchEvent()

        data object ClearUserNames : SearchEvent()
        data object ClearRepoNames : SearchEvent()

        data object AddReceivedUserNames : SearchEvent()
        data object AddReceivedRepoNames : SearchEvent()
    }

    sealed class SearchState {
        sealed class LoadState : SearchState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object RepoSuccess : LoadState()
            data object UserSuccess : LoadState()
            data object Error : LoadState()
        }

        data class RepoNames(val repoNames: ArrayList<RepoSearchResultModel>) : SearchState()
        data class UserNames(val userNames: ArrayList<UserNameModelItem>) : SearchState()
    }

    data class SearchStates(
        val searchState: SearchState.LoadState,
        val userNames: SearchState.UserNames,
        val receivedUserNames: SearchState.UserNames,
        val repoNames: SearchState.RepoNames,
        val receivedRepoNames: SearchState.RepoNames
    ) : UiState

    sealed class SearchEffect : UiEffect {

    }
}