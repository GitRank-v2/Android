package com.dragonguard.android.ui.profile.user

import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference

class ClientReposViewModel :
    BaseViewModel<ClientReposContract.ClientReposEvent, ClientReposContract.ClientReposStates, ClientReposContract.ClientReposEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): ClientReposContract.ClientReposStates {
        pref = getPref()
        repository = getRepository()
        return ClientReposContract.ClientReposStates(
            ClientReposContract.ClientReposState.LoadState.Initial,
            ClientReposContract.ClientReposState.Token(pref.getJwtToken("")),
            ClientReposContract.ClientReposState.GithubOrgRepos(GithubOrgReposModel())
        )
    }

    override fun handleEvent(event: ClientReposContract.ClientReposEvent) {
        when (event) {
            is ClientReposContract.ClientReposEvent.GetGithubOrgRepos -> {
                setState { copy(loadState = ClientReposContract.ClientReposState.LoadState.Loading) }
                val result = repository.userGitOrgRepoList(event.orgName)
                result?.let {
                    setState {
                        copy(
                            loadState = ClientReposContract.ClientReposState.LoadState.Success,
                            githubOrgRepos = ClientReposContract.ClientReposState.GithubOrgRepos(it)
                        )
                    }
                }
            }
        }
    }

    fun getGithubOrgRepos(orgName: String) {
        setEvent(ClientReposContract.ClientReposEvent.GetGithubOrgRepos(orgName))
    }
}