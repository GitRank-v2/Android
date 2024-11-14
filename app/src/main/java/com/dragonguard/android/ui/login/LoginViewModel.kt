package com.dragonguard.android.ui.login

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() :
    BaseViewModel<LoginContract.LoginEvent, LoginContract.LoginStates, LoginContract.LoginEffect>() {
    private lateinit var pref: IdPreference
    override fun createInitialState(): LoginContract.LoginStates {
        pref = getPref()
        return LoginContract.LoginStates(
            LoginContract.LoginState.LoginStat(null),
            LoginContract.LoginState.Token(pref.getJwtToken("")),
            LoginContract.LoginState.RefreshToken(pref.getRefreshToken("")),
            LoginContract.LoginState.Key("")
        )
    }

    override fun handleEvent(event: LoginContract.LoginEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginContract.LoginEvent.SetJwtToken -> {
                    pref.setJwtToken(event.token)
                    pref.setRefreshToken(event.refreshToken)
                    setState {
                        copy(
                            token = LoginContract.LoginState.Token(event.token),
                            refreshToken = LoginContract.LoginState.RefreshToken(event.refreshToken)
                        )
                    }
                }

                is LoginContract.LoginEvent.GetJwtToken -> {
                    setState {
                        copy(
                            token = LoginContract.LoginState.Token(pref.getJwtToken("")),
                            refreshToken = LoginContract.LoginState.RefreshToken(
                                pref.getRefreshToken(
                                    ""
                                )
                            )
                        )
                    }
                }
            }
        }
    }


    fun setJwtToken(token: String, refreshToken: String) {
        setEvent(LoginContract.LoginEvent.SetJwtToken(token, refreshToken))
    }

    fun getJwtToken() {
        setEvent(LoginContract.LoginEvent.GetJwtToken)
    }

}