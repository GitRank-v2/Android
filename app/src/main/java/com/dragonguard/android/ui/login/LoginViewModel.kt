package com.dragonguard.android.ui.login

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: ApiRepository, private val pref: IdPreference) :
    BaseViewModel<LoginContract.LoginEvent, LoginContract.LoginStates, LoginContract.LoginEffect>() {


    override fun createInitialState(): LoginContract.LoginStates {
        return LoginContract.LoginStates(
            LoginContract.LoginState.LoginStat(null),
            LoginContract.LoginState.Token(""),
            LoginContract.LoginState.RefreshToken(""),
            LoginContract.LoginState.Key("")
        )
    }

    override fun handleEvent(event: LoginContract.LoginEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginContract.LoginEvent.CheckLoginState -> {
                    val result = repository.getLoginState(event.token)
                    setState { copy(loginState = LoginContract.LoginState.LoginStat(result)) }
                }

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

                is LoginContract.LoginEvent.SetKey -> {
                    pref.setKey(event.key)
                    setState { copy(key = LoginContract.LoginState.Key(event.key)) }
                }

                is LoginContract.LoginEvent.GetKey -> {
                    setState { copy(key = LoginContract.LoginState.Key(pref.getKey(""))) }
                }
            }
        }
    }

    fun checkLoginState(token: String) {
        setEvent(LoginContract.LoginEvent.CheckLoginState(token))
    }

    fun setJwtToken(token: String, refreshToken: String) {
        setEvent(LoginContract.LoginEvent.SetJwtToken(token, refreshToken))
    }

    fun getJwtToken() {
        setEvent(LoginContract.LoginEvent.GetJwtToken)
    }

    fun setKey(key: String) {
        setEvent(LoginContract.LoginEvent.SetKey(key))
    }
}