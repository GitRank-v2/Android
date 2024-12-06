package com.dragonguard.android.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.data.repository.main.MainRepository
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
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel<MainContract.MainEvent, MainContract.MainStates, MainContract.MainActivityEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): MainContract.MainStates {
        pref = getPref()
        return MainContract.MainStates(
            loadState = LoadState.INIT,
            userInfo = MainContract.MainState.UserInfo(
                UserInfoModel()
            ),
            clickSearch = MainContract.MainState.ClickSearch(false),
            clickUserIcon = MainContract.MainState.ClickUserIcon(false),
            newAccessToken = MainContract.MainState.NewAccessToken(pref.getJwtToken("")),
            newRefreshToken = MainContract.MainState.NewRefreshToken(pref.getRefreshToken("")),
            repeatState = MainContract.MainState.RepeatState(false),
            refreshAmount = MainContract.MainState.RefreshAmount(emptyList())
        )
    }

    override fun handleEvent(event: MainContract.MainEvent) {
        viewModelScope.launch {
            when (event) {
                is MainContract.MainEvent.GetUserInfo -> {
                    Log.d("MainViewModel", pref.getJwtToken(""))
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getUserInfo().onSuccess {
                        Log.d("user success", it.toString())
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                userInfo = MainContract.MainState.UserInfo(it)
                            )
                        }
                    }.onFail {
                        //setState { copy(loadState = LoadState.ERROR) }
                    }.onError {
                        setState { copy(loadState = LoadState.LOGIN_FAIL) }
                        Log.d("MainViewModel", "Login Fail")
                        Log.d("MainViewModel", it.message.toString())
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
                            pref.setJwtToken(it.access_token)
                            pref.setRefreshToken(it.refresh_token)
                            setState {
                                copy(
                                    newAccessToken = MainContract.MainState.NewAccessToken(it.access_token),
                                    newRefreshToken = MainContract.MainState.NewRefreshToken(it.refresh_token)
                                )
                            }
                        }.onError {
                            Log.d("Login Error", it.message.toString())
                            setEffect { MainContract.MainActivityEffect.LoginError }
                        }.onFail {
                            setEffect { MainContract.MainActivityEffect.LoginError }
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

                is MainContract.MainEvent.SetFinish -> {
                    setState { copy(loadState = LoadState.FINISH) }
                }

                is MainContract.MainEvent.RefreshAmount -> {
                    repository.updateGitContribution().onSuccess {
                        Log.d("refresh viewModel", "refresh")
                        repository.updateGitContributions().onSuccess {
                            Log.d("refresh viewModel", "refreshes")
                            setState {
                                copy(
                                    loadState = LoadState.REFRESH,
                                    refreshAmount = MainContract.MainState.RefreshAmount(it)
                                )
                            }
                        }.onFail {
                            Log.d("refresh viewModel", "fail")
                        }.onError {
                            Log.d("refresh viewModel", it.message.toString())
                        }
                    }.onFail {

                    }

                }

                is MainContract.MainEvent.ProfileImageLoaded -> {
                    setState { copy(loadState = LoadState.IMAGE_LOADED) }
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

    fun setFinish() {
        setEvent(MainContract.MainEvent.SetFinish)
    }

    fun refreshAmount() {
        setEvent(MainContract.MainEvent.RefreshAmount)
    }

    fun profileImageLoaded() {
        setEvent(MainContract.MainEvent.ProfileImageLoaded)
    }
}