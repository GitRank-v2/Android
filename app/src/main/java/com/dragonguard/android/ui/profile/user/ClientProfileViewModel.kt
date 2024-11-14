package com.dragonguard.android.ui.profile.user

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    private val pref: IdPreference,
    private val repository: ApiRepository
) : BaseViewModel<ClientProfileContract.ClientProfileEvent, ClientProfileContract.ClientProfileStates, ClientProfileContract.ClientProfileEffect>() {
    override fun createInitialState(): ClientProfileContract.ClientProfileStates {
        return ClientProfileContract.ClientProfileStates(
            LoadState.INIT,
            ClientProfileContract.ClientProfileState.ClientDetail(ClientDetailModel()),
            ClientProfileContract.ClientProfileState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: ClientProfileContract.ClientProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is ClientProfileContract.ClientProfileEvent.GetClientDetail -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getClientDetails().onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
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