package com.dragonguard.android.ui.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.repository.login.LoginRepository
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
class LoginViewModel @Inject constructor(private val repository: LoginRepository) :
    BaseViewModel<LoginContract.LoginEvent, LoginContract.LoginStates, LoginContract.LoginEffect>() {
    private lateinit var pref: IdPreference
    override fun createInitialState(): LoginContract.LoginStates {
        pref = getPref()
        return LoginContract.LoginStates(
            LoadState.INIT,
        )
    }

    override fun handleEvent(event: LoginContract.LoginEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginContract.LoginEvent.SetJwtToken -> {
                    pref.setJwtToken(event.token)
                    pref.setRefreshToken(event.refreshToken)
                }

                is LoginContract.LoginEvent.RefreshToken -> {
                    Log.d("Login", "${pref.getJwtToken("")} ${pref.getRefreshToken("")}")
                    if (pref.getJwtToken("").isEmpty() || pref.getRefreshToken("").isEmpty()) {
                        pref.setJwtToken("")
                        pref.setRefreshToken("")
                        setState { copy(loginState = LoadState.LOGIN_FAIL) }
                    } else {
                        //setState { copy(loginState = LoadState.SUCCESS) }
                        repository.refreshToken(pref.getJwtToken(""), pref.getRefreshToken(""))
                            .onSuccess {
                                pref.setJwtToken(it.access_token)
                                pref.setRefreshToken(it.refresh_token)
                                setState { copy(loginState = LoadState.SUCCESS) }
                            }.onError {
                                Log.d("Login Error", it.message.toString())
                            }.onFail {
                                Log.d("Login Error", it.toString())
                            }
                    }
                }

                is LoginContract.LoginEvent.LogOut -> {
                    pref.setJwtToken("")
                    pref.setRefreshToken("")
                    setState { copy(loginState = LoadState.LOGIN_FAIL) }
                }
            }
        }
    }


    fun setJwtToken(token: String, refreshToken: String) {
        setEvent(LoginContract.LoginEvent.SetJwtToken(token, refreshToken))
    }

    fun refreshToken() {
        setEvent(LoginContract.LoginEvent.RefreshToken)
    }

    fun logOut() {
        setEvent(LoginContract.LoginEvent.LogOut)
    }

}