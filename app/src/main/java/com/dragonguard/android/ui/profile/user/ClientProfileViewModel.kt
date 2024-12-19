package com.dragonguard.android.ui.profile.user

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.repository.profile.user.ClientProfileRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    private val repository: ClientProfileRepository
) : BaseViewModel<ClientProfileContract.ClientProfileEvent, ClientProfileContract.ClientProfileStates, ClientProfileContract.ClientProfileEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): ClientProfileContract.ClientProfileStates {
        pref = getPref()
        return ClientProfileContract.ClientProfileStates(
            LoadState.INIT,
            ClientProfileContract.ClientProfileState.ClientRepository(emptyList()),
            ClientProfileContract.ClientProfileState.ClientOrg(emptyList())
        )
    }

    override fun handleEvent(event: ClientProfileContract.ClientProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is ClientProfileContract.ClientProfileEvent.GetClientDetail -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getClientRepository().onSuccess { repo ->
                        repository.getClientOrg().onSuccess { org ->
                            setState {
                                copy(
                                    loadState = LoadState.SUCCESS,
                                    clientRepository = ClientProfileContract.ClientProfileState.ClientRepository(
                                        repo
                                    ),
                                    clientOrg = ClientProfileContract.ClientProfileState.ClientOrg(
                                        org
                                    )
                                )
                            }
                        }.onError {

                        }
                    }.onError {

                    }
                }
            }
        }
    }

    fun getClientDetail() {
        setEvent(ClientProfileContract.ClientProfileEvent.GetClientDetail)
    }
}