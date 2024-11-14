package com.dragonguard.android.ui.search.repo

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.repository.search.repo.RepoContributorsRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoContributorsViewModel @Inject constructor(
    private val repository: RepoContributorsRepository
) : BaseViewModel<RepoContributorsContract.RepoContributorsEvent, RepoContributorsContract.RepoContributorsStates, RepoContributorsContract.RepoContributorsEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): RepoContributorsContract.RepoContributorsStates {
        pref = getPref()
        return RepoContributorsContract.RepoContributorsStates(
            LoadState.INIT,
            RepoContributorsContract.RepoContributorsState.RepoContributeState(RepoContributorsModel()),
            RepoContributorsContract.RepoContributorsState.TokenState(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: RepoContributorsContract.RepoContributorsEvent) {
        viewModelScope.launch {
            when (event) {
                is RepoContributorsContract.RepoContributorsEvent.GetRepoContributors -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getRepoContributors(event.repoName).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                repoState = RepoContributorsContract.RepoContributorsState.RepoContributeState(
                                    it
                                )
                            )
                        }
                    }.onFail {

                    }

                }
            }
        }
    }

    fun getRepoContributors(repoName: String) {
        setEvent(RepoContributorsContract.RepoContributorsEvent.GetRepoContributors(repoName))
    }

}