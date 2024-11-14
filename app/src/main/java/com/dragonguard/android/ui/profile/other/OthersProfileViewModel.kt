package com.dragonguard.android.ui.profile.other

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.model.detail.UserProfileModel
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
class OthersProfileViewModel @Inject constructor(
    private val pref: IdPreference,
    private val repository: ApiRepository
) : BaseViewModel<OthersProfileContract.UserProfileEvent, OthersProfileContract.UserProfileStates, OthersProfileContract.UserProfileEffect>() {
    override fun createInitialState(): OthersProfileContract.UserProfileStates {
        return OthersProfileContract.UserProfileStates(
            LoadState.INIT,
            OthersProfileContract.UserProfileState.Token(pref.getJwtToken("")),
            OthersProfileContract.UserProfileState.UserProfile(UserProfileModel())
        )
    }

    override fun handleEvent(event: OthersProfileContract.UserProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is OthersProfileContract.UserProfileEvent.GetOthersProfile -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.otherProfile(event.name).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
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