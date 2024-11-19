package com.dragonguard.android.ui.profile.user

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.data.repository.profile.user.ClientReposRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClientReposViewModel @Inject constructor(
    private val repository: ClientReposRepository
) : BaseViewModel<ClientReposContract.ClientReposEvent, ClientReposContract.ClientReposStates, ClientReposContract.ClientReposEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): ClientReposContract.ClientReposStates {
        pref = getPref()
        return ClientReposContract.ClientReposStates(
            LoadState.INIT,
            ClientReposContract.ClientReposState.Token(pref.getJwtToken("")),
            ClientReposContract.ClientReposState.GithubOrgRepos(GithubOrgReposModel())
        )
    }

    override fun handleEvent(event: ClientReposContract.ClientReposEvent) {
        viewModelScope.launch {
            when (event) {
                is ClientReposContract.ClientReposEvent.GetGithubOrgRepos -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.userGitOrgRepoList(event.orgName).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                githubOrgRepos = ClientReposContract.ClientReposState.GithubOrgRepos(
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

    fun getGithubOrgRepos(orgName: String) {
        setEvent(ClientReposContract.ClientReposEvent.GetGithubOrgRepos(orgName))
    }
}