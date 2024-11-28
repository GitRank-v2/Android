package com.dragonguard.android.ui.profile.other

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.repository.profile.other.OthersProfileRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersProfileViewModel @Inject constructor(
    private val repository: OthersProfileRepository
) : BaseViewModel<OthersProfileContract.UserProfileEvent, OthersProfileContract.UserProfileStates, OthersProfileContract.UserProfileEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): OthersProfileContract.UserProfileStates {
        pref = getPref()
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

                is OthersProfileContract.UserProfileEvent.GetUserProfile -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getUserProfile().onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                userProfile = OthersProfileContract.UserProfileState.UserProfile(it)
                            )
                        }
                    }.onFail {

                    }.onError {
                        Log.d("error", it.message.toString())
                    }
                }
            }
        }
    }

    fun getOthersProfile(name: String) {
        setEvent(OthersProfileContract.UserProfileEvent.GetOthersProfile(name))
    }

    fun getUserProfile() {
        setEvent(OthersProfileContract.UserProfileEvent.GetUserProfile)
    }
}