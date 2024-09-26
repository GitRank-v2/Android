package com.dragonguard.android.ui.search.repo

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class RepoContributorsViewModel :
    BaseViewModel<RepoContributorsContract.RepoContributorsEvent, RepoContributorsContract.RepoContributorsStates, RepoContributorsContract.RepoContributorsEffect>() {
    private lateinit var repository: ApiRepository
    private lateinit var pref: IdPreference
    override fun createInitialState(): RepoContributorsContract.RepoContributorsStates {
        repository = getRepository()
        pref = getPref()
        return RepoContributorsContract.RepoContributorsStates(
            RepoContributorsContract.RepoContributorsState.LoadState.Initial,
            RepoContributorsContract.RepoContributorsState.RepoContributeState(RepoContributorsModel()),
            RepoContributorsContract.RepoContributorsState.TokenState(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: RepoContributorsContract.RepoContributorsEvent) {
        viewModelScope.launch {
            when (event) {
                is RepoContributorsContract.RepoContributorsEvent.GetRepoContributors -> {
                    setState { copy(loadState = RepoContributorsContract.RepoContributorsState.LoadState.Loading) }
                    val result =
                        repository.getRepoContributors(event.repoName)
                    setState {
                        copy(
                            loadState = RepoContributorsContract.RepoContributorsState.LoadState.Success,
                            repoState = RepoContributorsContract.RepoContributorsState.RepoContributeState(
                                result
                            )
                        )
                    }
                }
            }
        }
    }

    fun getRepoContributors(repoName: String) {
        setEvent(RepoContributorsContract.RepoContributorsEvent.GetRepoContributors(repoName))
    }

}