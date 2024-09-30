package com.dragonguard.android.ui.menu.org.regist

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class RegistOrgViewModel :
    BaseViewModel<RegistOrgContract.RegistOrgEvent, RegistOrgContract.RegistOrgStates, RegistOrgContract.RegistOrgEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository

    override fun createInitialState(): RegistOrgContract.RegistOrgStates {
        pref = getPref()
        repository = getRepository()
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
                    setState { copy(state = LoadState.LOADING) }
                    repository.postRegistOrg(
                        RegistOrgModel(
                            event.orgName,
                            event.orgType,
                            event.orgEndPoint
                        )
                    ).onSuccess {
                        setState {
                            copy(
                                state = LoadState.SUCCESS,
                                registResult = RegistOrgContract.RegistOrgState.RegistResult(it)
                            )
                        }
                    }.onFail {

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