package com.dragonguard.android.ui.search.repo

import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class RepoContributorsContract {
    sealed class RepoContributorsEvent : UiEvent {
        data class GetRepoContributors(val repoName: String) : RepoContributorsEvent()
    }

    sealed class RepoContributorsState {
        data class RepoContributeState(val repoState: RepoContributorsModel) :
            RepoContributorsState()

        data class TokenState(val token: String) : RepoContributorsState()
    }

    data class RepoContributorsStates(
        val loadState: LoadState,
        val repoState: RepoContributorsState.RepoContributeState,
        val token: RepoContributorsState.TokenState
    ) : UiState

    sealed class RepoContributorsEffect : UiEffect {
        data object ShowToast : RepoContributorsEffect()

    }


}