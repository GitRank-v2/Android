package com.dragonguard.android.ui.compare.compare

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.repository.compare.compare.RepoCompareRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoCompareViewModel @Inject constructor(
    private val repository: RepoCompareRepository
) : BaseViewModel<RepoCompareContract.RepoCompareEvent, RepoCompareContract.RepoCompareStates, RepoCompareContract.RepoCompareEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): RepoCompareContract.RepoCompareStates {
        pref = getPref()
        return RepoCompareContract.RepoCompareStates(
            LoadState.INIT,
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
                    setState { copy(loadState = LoadState.LOADING) }
                    Log.d(
                        "RepoCompareViewModel",
                        "member repo1: ${event.repo1}, repo2: ${event.repo2}"
                    )
                    repository.postCompareRepoMembersRequest(event.repo1, event.repo2).onSuccess {
                        Log.d("RepoCompareViewModel", "repoMembers: $it")
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                repoMembers = RepoCompareContract.RepoCompareState.RepoMembers(it)
                            )
                        }
                    }.onError {
                        Log.d("RepoCompareViewModel", "compare member error: ${it.message}")
                    }


                }

                is RepoCompareContract.RepoCompareEvent.RequestCompareRepo -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    Log.d(
                        "RepoCompareViewModel",
                        "repo repo1: ${event.repo1}, repo2: ${event.repo2}"
                    )
                    repository.postCompareRepoRequest(event.repo1, event.repo2).onSuccess {
                        Log.d("RepoCompareViewModel", "compare repo: $it")
                        setState {
                            copy(
                                loadState = LoadState.REPO_SUCCESS,
                                repo = RepoCompareContract.RepoCompareState.Repo(it)
                            )
                        }
                    }.onError {
                        Log.d("RepoCompareViewModel", "compare repo error: ${it.message}")
                    }
                }

                is RepoCompareContract.RepoCompareEvent.SetFinish -> {
                    setState { copy(loadState = LoadState.FINISH) }
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

    fun setFinish() {
        setEvent(RepoCompareContract.RepoCompareEvent.SetFinish)
    }
}