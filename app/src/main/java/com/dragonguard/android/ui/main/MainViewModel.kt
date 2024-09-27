package com.dragonguard.android.ui.main

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class MainViewModel :
    BaseViewModel<MainContract.MainEvent, MainContract.MainStates, MainContract.MainActivityEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): MainContract.MainStates {
        pref = getPref()
        repository = getRepository()
        return MainContract.MainStates(
            loadState = MainContract.MainState.LoadState.Initial,
            userInfo = MainContract.MainState.UserInfo(
                UserInfoModel()
            ),
            clickSearch = MainContract.MainState.ClickSearch(false),
            clickUserIcon = MainContract.MainState.ClickUserIcon(false),
            newAccessToken = MainContract.MainState.NewAccessToken(pref.getJwtToken("")),
            newRefreshToken = MainContract.MainState.NewRefreshToken(pref.getRefreshToken("")),
            repeatState = MainContract.MainState.RepeatState(false)
        )
    }

    override fun handleEvent(event: MainContract.MainEvent) {
        viewModelScope.launch {
            when (event) {
                is MainContract.MainEvent.GetUserInfo -> {
                    setState {
                        copy(
                            loadState = MainContract.MainState.LoadState.Loading
                        )
                    }
                    repository.getUserInfo().onSuccess {
                        setState {
                            copy(
                                loadState = MainContract.MainState.LoadState.Success,
                                userInfo = MainContract.MainState.UserInfo(it)
                            )
                        }
                    }.onFail {

                    }
                }

                is MainContract.MainEvent.ClickSearch -> {
                    setState {
                        copy(
                            clickSearch = MainContract.MainState.ClickSearch(
                                true
                            )
                        )
                    }
                }

                is MainContract.MainEvent.GetNewToken -> {
                    repository.getNewAccessToken(pref.getJwtToken(""), pref.getRefreshToken(""))
                        .onSuccess {
                            setState {
                                copy(
                                    newAccessToken = MainContract.MainState.NewAccessToken(it.access_token),
                                    newRefreshToken = MainContract.MainState.NewRefreshToken(it.refresh_token)
                                )
                            }
                        }.onFail {

                    }

                }

                is MainContract.MainEvent.Logout -> {
                    pref.setJwtToken("")
                    pref.setRefreshToken("")
                    pref.setPostAddress(false)

                }

                is MainContract.MainEvent.ClickUserIcon -> {
                    setState { copy(clickUserIcon = MainContract.MainState.ClickUserIcon(true)) }
                }

                is MainContract.MainEvent.SetRepeat -> {
                    setState { copy(repeatState = MainContract.MainState.RepeatState(event.repeat)) }
                }
            }
        }
    }

    fun getUserInfo() {
        setEvent(MainContract.MainEvent.GetUserInfo)
    }

    fun clickSearch() {
        setEvent(MainContract.MainEvent.ClickSearch)
    }

    fun getNewToken() {
        setEvent(MainContract.MainEvent.GetNewToken)
    }

    fun logout() {
        setEvent(MainContract.MainEvent.Logout)
    }

    fun clickUserIcon() {
        setEvent(MainContract.MainEvent.ClickUserIcon)
    }

    fun setRepeat(repeat: Boolean) {
        setEvent(MainContract.MainEvent.SetRepeat(repeat))
    }

}