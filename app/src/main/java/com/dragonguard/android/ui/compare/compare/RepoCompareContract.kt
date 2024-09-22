package com.dragonguard.android.ui.compare.compare

import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class RepoCompareContract {
    sealed class RepoCompareEvent : UiEvent {
        data class RequestCompareRepoMembers(val repo1: String, val repo2: String) :
            RepoCompareEvent()

        data class RequestCompareRepo(val repo1: String, val repo2: String) : RepoCompareEvent()
    }

    sealed class RepoCompareState {
        sealed class LoadState : RepoCompareState() {
            object Initial : LoadState()
            object Loading : LoadState()
            object Success : LoadState()
            object Error : LoadState()
        }

        data class Token(val token: String) : RepoCompareState()
        data class RepoMembers(val repoMembers: CompareRepoMembersResponseModel) :
            RepoCompareState()

        data class Repo(val repo: CompareRepoResponseModel) : RepoCompareState()
    }

    data class RepoCompareStates(
        val loadState: RepoCompareState.LoadState,
        val token: RepoCompareState.Token,
        val repoMembers: RepoCompareState.RepoMembers,
        val repo: RepoCompareState.Repo
    ) : UiState

    sealed class RepoCompareEffect : UiEffect {

    }
}