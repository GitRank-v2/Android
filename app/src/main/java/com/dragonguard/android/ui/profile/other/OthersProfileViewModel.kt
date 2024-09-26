package com.dragonguard.android.ui.profile.other

import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference

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
        when (event) {
            is OthersProfileContract.UserProfileEvent.GetOthersProfile -> {
                setState { copy(loadState = OthersProfileContract.UserProfileState.LoadState.Loading) }
                val result = repository.otherProfile(event.name)
                result?.let {
                    setState {
                        copy(
                            loadState = OthersProfileContract.UserProfileState.LoadState.Success,
                            userProfile = OthersProfileContract.UserProfileState.UserProfile(it)
                        )
                    }
                }
            }
        }
    }

    fun getOthersProfile(name: String) {
        setEvent(OthersProfileContract.UserProfileEvent.GetOthersProfile(name))
    }
}