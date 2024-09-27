package com.dragonguard.android.ui.profile.other

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class OthersProfileViewModel :
    BaseViewModel<OthersProfileContract.UserProfileEvent, OthersProfileContract.UserProfileStates, OthersProfileContract.UserProfileEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): OthersProfileContract.UserProfileStates {
        pref = getPref()
        repository = getRepository()
        return OthersProfileContract.UserProfileStates(
            OthersProfileContract.UserProfileState.LoadState.Initial,
            OthersProfileContract.UserProfileState.Token(pref.getJwtToken("")),
            OthersProfileContract.UserProfileState.UserProfile(UserProfileModel())
        )
    }

    override fun handleEvent(event: OthersProfileContract.UserProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is OthersProfileContract.UserProfileEvent.GetOthersProfile -> {
                    setState { copy(loadState = OthersProfileContract.UserProfileState.LoadState.Loading) }
                    repository.otherProfile(event.name).onSuccess {
                        setState {
                            copy(
                                loadState = OthersProfileContract.UserProfileState.LoadState.Success,
                                userProfile = OthersProfileContract.UserProfileState.UserProfile(it)
                            )
                        }
                    }.onFail {

                    }
                }
            }
        }
    }

    fun getOthersProfile(name: String) {
        setEvent(OthersProfileContract.UserProfileEvent.GetOthersProfile(name))
    }
}