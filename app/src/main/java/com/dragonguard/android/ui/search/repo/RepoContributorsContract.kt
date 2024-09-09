package com.dragonguard.android.ui.search.repo

import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class RepoContributorsContract {
    sealed class RepoContributorsEvent : UiEvent {
        data class GetRepoContributors(val repoName: String) : RepoContributorsEvent()
    }

    sealed class RepoContributorsState {
        sealed class LoadState : RepoContributorsState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object Success : LoadState()

        }

        data class RepoContributeState(val repoState: RepoContributorsModel) :
            RepoContributorsState()

        data class TokenState(val token: String) : RepoContributorsState()
    }

    data class RepoContributorsStates(
        val loadState: RepoContributorsState.LoadState,
        val repoState: RepoContributorsState.RepoContributeState,
        val token: RepoContributorsState.TokenState
    ) : UiState

    sealed class RepoContributorsEffect : UiEffect {
        data object ShowToast : RepoContributorsEffect()

    }


}