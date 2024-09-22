package com.dragonguard.android.ui.profile.user

import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class ClientReposContract {
    sealed class ClientReposEvent : UiEvent {
        data class GetGithubOrgRepos(val orgName: String) : ClientReposEvent()
    }

    sealed class ClientReposState {
        sealed class LoadState : ClientReposState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object Success : LoadState()
            data object Error : LoadState()
        }

        data class GithubOrgRepos(val githubOrgRepos: GithubOrgReposModel) :
            ClientReposState()

        data class Token(val token: String) : ClientReposState()
    }

    data class ClientReposStates(
        val loadState: ClientReposState.LoadState,
        val token: ClientReposState.Token,
        val githubOrgRepos: ClientReposState.GithubOrgRepos
    ) : UiState

    sealed class ClientReposEffect : UiEffect {

    }
}