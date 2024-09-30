package com.dragonguard.android.ui.profile.user

import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class ClientReposContract {
    sealed class ClientReposEvent : UiEvent {
        data class GetGithubOrgRepos(val orgName: String) : ClientReposEvent()
    }

    sealed class ClientReposState {
        data class GithubOrgRepos(val githubOrgRepos: GithubOrgReposModel) :
            ClientReposState()

        data class Token(val token: String) : ClientReposState()
    }

    data class ClientReposStates(
        val loadState: LoadState,
        val token: ClientReposState.Token,
        val githubOrgRepos: ClientReposState.GithubOrgRepos
    ) : UiState

    sealed class ClientReposEffect : UiEffect {

    }
}