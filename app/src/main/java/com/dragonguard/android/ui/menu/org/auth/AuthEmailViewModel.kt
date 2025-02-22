package com.dragonguard.android.ui.menu.org.auth

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.repository.menu.org.auth.AuthEmailRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthEmailViewModel @Inject constructor(
    private val repository: AuthEmailRepository
) : BaseViewModel<AuthEmailContract.AuthEmailEvent, AuthEmailContract.AuthEmailStates, AuthEmailContract.AuthEmailEffect>() {
    override fun createInitialState(): AuthEmailContract.AuthEmailStates {
        return AuthEmailContract.AuthEmailStates(
            LoadState.LOADING,
            AuthEmailContract.AuthEmailState.CustomTimerDuration(MutableLiveData(MIllIS_IN_FUTURE)),
            AuthEmailContract.AuthEmailState.Timer(
                0,
                viewModelScope.launch(start = CoroutineStart.LAZY) {
                    withContext(Dispatchers.IO) {
                        currentState.timer.oldTime = System.currentTimeMillis()
                        while (currentState.customTimerDuration.timerDuration.value!! > 0L) {
                            val delayMills = System.currentTimeMillis() - currentState.timer.oldTime
                            if (delayMills == TICK_INTERVAL) {
                                currentState.customTimerDuration.timerDuration.postValue(
                                    currentState.customTimerDuration.timerDuration.value!! - delayMills
                                )
                                currentState.timer.oldTime = System.currentTimeMillis()
                            }
                        }
                    }
                }),
            AuthEmailContract.AuthEmailState.CountDownTimer(setUpCountDownTimer()),
            AuthEmailContract.AuthEmailState.EmailAuthId(-1),
            AuthEmailContract.AuthEmailState.ResetTimer(false),
            AuthEmailContract.AuthEmailState.RemainTime(""),
            AuthEmailContract.AuthEmailState.TimeOver(false)
        )
    }

    private fun setUpCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(
            MIllIS_IN_FUTURE,
            TICK_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val minute = millisUntilFinished / 60000L
                val second = millisUntilFinished % 60000L / 1000L
                setState {
                    copy(
                        remainTime = AuthEmailContract.AuthEmailState.RemainTime("$minute:$second")
                    )
                }
            }

            override fun onFinish() {
                setState {
                    copy(
                        timeOver = AuthEmailContract.AuthEmailState.TimeOver(true)
                    )
                }
            }
        }
    }

    override fun handleEvent(event: AuthEmailContract.AuthEmailEvent) {
        viewModelScope.launch {
            when (event) {
                is AuthEmailContract.AuthEmailEvent.RequestAuthEmail -> {
                    Log.d("AuthEmailViewModel", "request email: ${event.email} ${event.orgId}")
                    repository.addOrgMember(AddOrgMemberModel(event.email, event.orgId)).onSuccess {
                        Log.d("AuthEmailViewModel", "request email: $it")
                        if (it != -1L) {
                            setState {
                                copy(
                                    state = LoadState.LOADING,
                                    emailAuthId = AuthEmailContract.AuthEmailState.EmailAuthId(it),
                                    countDownTimer = AuthEmailContract.AuthEmailState.CountDownTimer(
                                        setUpCountDownTimer()
                                    ),
                                    timeOver = AuthEmailContract.AuthEmailState.TimeOver(false)
                                )
                            }
                            currentState.timer.timer.start()
                        }
                    }.onFail {

                    }
                }

                is AuthEmailContract.AuthEmailEvent.CheckEmailCode -> {
                    Log.d(
                        "AuthEmailViewModel",
                        "check email code: ${currentState.emailAuthId.emailAuthId} ${event.code} ${event.orgId}"
                    )
                    repository.emailAuthResult(
                        currentState.emailAuthId.emailAuthId,
                        event.code,
                        event.orgId
                    ).onSuccess {
                        if (it) {
                            setState { copy(state = LoadState.SUCCESS) }
                        } else {
                            setState { copy(state = LoadState.ERROR) }
                        }
                    }.onFail {

                    }.onError {
                        Log.d("AuthEmailViewModel", "handleEvent: ${it.message}")
                    }
                }

                is AuthEmailContract.AuthEmailEvent.DeleteLateEmailCode -> {
                    repository.deleteEmailCode(currentState.emailAuthId.emailAuthId).onSuccess {
                        setState {
                            copy(
                                resetTimer = AuthEmailContract.AuthEmailState.ResetTimer(true),
                            )
                        }
                        currentState.countDownTimer.countDownTimer.cancel()
                    }.onFail {
                        setState {
                            copy(
                                resetTimer = AuthEmailContract.AuthEmailState.ResetTimer(false),
                            )
                        }
                    }
                }

                is AuthEmailContract.AuthEmailEvent.ResendEmailAuth -> {
                    repository.sendEmailAuth(currentState.emailAuthId.emailAuthId).onSuccess {
                        if (it != -1L) {
                            setState {
                                copy(
                                    state = LoadState.LOADING,
                                    emailAuthId = AuthEmailContract.AuthEmailState.EmailAuthId(it),
                                    countDownTimer = AuthEmailContract.AuthEmailState.CountDownTimer(
                                        setUpCountDownTimer()
                                    ),
                                    timeOver = AuthEmailContract.AuthEmailState.TimeOver(false)
                                )
                            }
                        }
                    }.onFail {

                    }

                }
            }
        }

    }

    companion object {
        private const val MIllIS_IN_FUTURE = 300000L
        private const val TICK_INTERVAL = 1000L
    }

    fun requestAuthEmail(orgId: Long, email: String) {
        setEvent(AuthEmailContract.AuthEmailEvent.RequestAuthEmail(orgId, email))
    }

    fun checkEmailCode(code: String, orgId: Long) {
        setEvent(AuthEmailContract.AuthEmailEvent.CheckEmailCode(code, orgId))
    }

    fun deleteLateEmailCode() {
        setEvent(AuthEmailContract.AuthEmailEvent.DeleteLateEmailCode)
    }

    fun reSendEmailAuth() {
        setEvent(AuthEmailContract.AuthEmailEvent.ResendEmailAuth)
    }
}