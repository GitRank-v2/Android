package com.dragonguard.android.ui.menu.org.regist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.data.repository.menu.org.regist.RegistOrgRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistOrgViewModel @Inject constructor(
    private val repository: RegistOrgRepository
) : BaseViewModel<RegistOrgContract.RegistOrgEvent, RegistOrgContract.RegistOrgStates, RegistOrgContract.RegistOrgEffect>() {
    override fun createInitialState(): RegistOrgContract.RegistOrgStates {
        return RegistOrgContract.RegistOrgStates(
            LoadState.INIT,
            RegistOrgContract.RegistOrgState.Token(""),
            RegistOrgContract.RegistOrgState.RegistResult(RegistOrgResultModel())
        )
    }

    override fun handleEvent(event: RegistOrgContract.RegistOrgEvent) {
        viewModelScope.launch {
            when (event) {
                is RegistOrgContract.RegistOrgEvent.RequestRegistOrg -> {
                    Log.d(
                        "RegistOrgViewModel",
                        "${event.orgName} ${event.orgType} ${event.orgEndPoint}"
                    )
                    setState { copy(state = LoadState.LOADING) }
                    repository.postRegistOrg(
                        RegistOrgModel(
                            event.orgEndPoint,
                            event.orgName,
                            event.orgType
                        )
                    ).onSuccess {
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                registResult = RegistOrgContract.RegistOrgState.RegistResult(it)
                            )
                        }
                    }.onFail {

                    }.onError {
                        Log.d("RegistOrgViewModel", it.message.toString())
                    }
                }
            }
        }
    }

    fun requestRegistOrg(orgName: String, orgType: String, orgEndPoint: String) {
        setEvent(
            RegistOrgContract.RegistOrgEvent.RequestRegistOrg(
                orgName,
                orgType,
                orgEndPoint
            )
        )
    }
}