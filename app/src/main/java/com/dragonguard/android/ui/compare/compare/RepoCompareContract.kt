package com.dragonguard.android.ui.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState

class RepoCompareContract {
    sealed class RepoCompareEvent : UiEvent {
        data class RequestCompareRepoMembers(val repo1: String, val repo2: String) :
            RepoCompareEvent()

        data class RequestCompareRepo(val repo1: String, val repo2: String) : RepoCompareEvent()
        data object SetFinish : RepoCompareEvent()
    }

    sealed class RepoCompareState {
        data class Token(val token: String) : RepoCompareState()
        data class RepoMembers(val repoMembers: CompareRepoMembersResponseModel) :
            RepoCompareState()

        data class Repo(val repo: CompareRepoResponseModel) : RepoCompareState()
    }

    data class RepoCompareStates(
        val loadState: LoadState,
        val token: RepoCompareState.Token,
        val repoMembers: RepoCompareState.RepoMembers,
        val repo: RepoCompareState.Repo
    ) : UiState

    sealed class RepoCompareEffect : UiEffect {

    }
}