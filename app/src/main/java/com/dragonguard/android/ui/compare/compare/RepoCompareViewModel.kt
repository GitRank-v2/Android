package com.dragonguard.android.ui.compare.compare

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class RepoCompareViewModel :
    BaseViewModel<RepoCompareContract.RepoCompareEvent, RepoCompareContract.RepoCompareStates, RepoCompareContract.RepoCompareEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): RepoCompareContract.RepoCompareStates {
        pref = getPref()
        repository = getRepository()
        return RepoCompareContract.RepoCompareStates(
            RepoCompareContract.RepoCompareState.LoadState.Initial,
            RepoCompareContract.RepoCompareState.Token(pref.getJwtToken("")),
            RepoCompareContract.RepoCompareState.RepoMembers(
                CompareRepoMembersResponseModel(
                    null,
                    null
                )
            ),
            RepoCompareContract.RepoCompareState.Repo(CompareRepoResponseModel(null, null))
        )
    }

    override fun handleEvent(event: RepoCompareContract.RepoCompareEvent) {
        viewModelScope.launch {
            when (event) {
                is RepoCompareContract.RepoCompareEvent.RequestCompareRepoMembers -> {
                    setState { copy(loadState = RepoCompareContract.RepoCompareState.LoadState.Loading) }
                    val result = repository.postCompareRepoMembersRequest(
                        CompareRepoRequestModel(
                            event.repo1,
                            event.repo2
                        ), currentState.token.token
                    )
                    setState {
                        copy(
                            loadState = RepoCompareContract.RepoCompareState.LoadState.Success,
                            repoMembers = RepoCompareContract.RepoCompareState.RepoMembers(result)
                        )
                    }

                }

                is RepoCompareContract.RepoCompareEvent.RequestCompareRepo -> {
                    setState { copy(loadState = RepoCompareContract.RepoCompareState.LoadState.Loading) }
                    val result = repository.postCompareRepoRequest(
                        CompareRepoRequestModel(
                            event.repo1,
                            event.repo2
                        ), currentState.token.token
                    )
                    setState {
                        copy(
                            loadState = RepoCompareContract.RepoCompareState.LoadState.Success,
                            repo = RepoCompareContract.RepoCompareState.Repo(result)
                        )
                    }
                }
            }
        }
    }

    fun requestCompareRepoMembers(repo1: String, repo2: String) {
        setEvent(RepoCompareContract.RepoCompareEvent.RequestCompareRepoMembers(repo1, repo2))
    }

    fun requestCompareRepo(repo1: String, repo2: String) {
        setEvent(RepoCompareContract.RepoCompareEvent.RequestCompareRepo(repo1, repo2))
    }
}