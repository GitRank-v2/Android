package com.dragonguard.android.ui.profile.user

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class ClientProfileViewModel :
    BaseViewModel<ClientProfileContract.ClientProfileEvent, ClientProfileContract.ClientProfileStates, ClientProfileContract.ClientProfileEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): ClientProfileContract.ClientProfileStates {
        pref = getPref()
        repository = getRepository()
        return ClientProfileContract.ClientProfileStates(
            ClientProfileContract.ClientProfileState.LoadState.Initial,
            ClientProfileContract.ClientProfileState.ClientDetail(ClientDetailModel()),
            ClientProfileContract.ClientProfileState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: ClientProfileContract.ClientProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is ClientProfileContract.ClientProfileEvent.GetClientDetail -> {
                    setState { copy(loadState = ClientProfileContract.ClientProfileState.LoadState.Loading) }
                    repository.getClientDetails().onSuccess {
                        setState {
                            copy(
                                loadState = ClientProfileContract.ClientProfileState.LoadState.Success,
                                clientDetail = ClientProfileContract.ClientProfileState.ClientDetail(
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

    fun getClientDetail() {
        setEvent(ClientProfileContract.ClientProfileEvent.GetClientDetail)
    }
}