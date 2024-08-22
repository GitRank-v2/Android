package com.dragonguard.android.ui.main

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: ApiRepository) :
    BaseViewModel<MainActivityContract.MainActivityEvent, MainActivityContract.MainActivityStates, MainActivityContract.MainActivityEffect>() {
    private val pref = getPref()

    override fun createInitialState(): MainActivityContract.MainActivityStates {
        return MainActivityContract.MainActivityStates(
            loadState = MainActivityContract.MainActivityState.LoadState.Initial,
            userInfo = MainActivityContract.MainActivityState.UserInfo(
                UserInfoModel()
            ),
            searchClicked = MainActivityContract.MainActivityState.SearchClicked(false),
            newAccessToken = MainActivityContract.MainActivityState.NewAccessToken(""),
            newRefreshToken = MainActivityContract.MainActivityState.NewRefreshToken("")
        )
    }

    override fun handleEvent(event: MainActivityContract.MainActivityEvent) {
        viewModelScope.launch {
            when (event) {
                is MainActivityContract.MainActivityEvent.GetUserInfo -> {
                    setState {
                        copy(
                            loadState = MainActivityContract.MainActivityState.LoadState.Loading
                        )
                    }
                    val userInfo = repository.getUserInfo(pref.getJwtToken(""))
                    setState {
                        copy(
                            loadState = MainActivityContract.MainActivityState.LoadState.Success,
                            userInfo = MainActivityContract.MainActivityState.UserInfo(
                                userInfo
                            )
                        )
                    }
                }

                is MainActivityContract.MainActivityEvent.SearchClick -> {
                    setState {
                        copy(
                            searchClicked = MainActivityContract.MainActivityState.SearchClicked(
                                true
                            )
                        )
                    }
                }

                is MainActivityContract.MainActivityEvent.GetNewToken -> {
                    val result =
                        repository.getNewAccessToken(pref.getJwtToken(""), pref.getRefreshToken(""))
                    setState {
                        copy(
                            newAccessToken = MainActivityContract.MainActivityState.NewAccessToken(
                                result.access_token
                            ),
                            newRefreshToken = MainActivityContract.MainActivityState.NewRefreshToken(
                                result.refresh_token
                            )
                        )
                    }
                }

                is MainActivityContract.MainActivityEvent.Logout -> {
                    pref.setJwtToken("")
                    pref.setRefreshToken("")
                    pref.setPostAddress(false)

                }
            }
        }
    }

    fun getUserInfo() {
        setEvent(MainActivityContract.MainActivityEvent.GetUserInfo)
    }

    fun searchClick() {
        setEvent(MainActivityContract.MainActivityEvent.SearchClick)
    }

    fun getNewToken() {
        setEvent(MainActivityContract.MainActivityEvent.GetNewToken)
    }

    fun logout() {
        setEvent(MainActivityContract.MainActivityEvent.Logout)
    }

}