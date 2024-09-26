package com.dragonguard.android.ui.menu.org.auth

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthEmailViewModel :
    BaseViewModel<AuthEmailContract.AuthEmailEvent, AuthEmailContract.AuthEmailStates, AuthEmailContract.AuthEmailEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): AuthEmailContract.AuthEmailStates {
        pref = getPref()
        repository = getRepository()
        return AuthEmailContract.AuthEmailStates(
            AuthEmailContract.AuthEmailState.LoadState.Initial,
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
                    val result = repository.addOrgMember(
                        AddOrgMemberModel(event.email, event.orgId)
                    )
                    if (result != -1L) {
                        setState {
                            copy(
                                emailAuthId = AuthEmailContract.AuthEmailState.EmailAuthId(result),
                                countDownTimer = AuthEmailContract.AuthEmailState.CountDownTimer(
                                    setUpCountDownTimer()
                                ),
                                timeOver = AuthEmailContract.AuthEmailState.TimeOver(false)
                            )
                        }
                        currentState.timer.timer.start()
                    }
                }

                is AuthEmailContract.AuthEmailEvent.CheckEmailCode -> {
                    val result =
                        repository.emailAuthResult(event.emailAuthId, event.code, event.orgId)
                    if (result) {
                        setState { copy(state = AuthEmailContract.AuthEmailState.LoadState.Success) }
                    } else {
                        setState { copy(state = AuthEmailContract.AuthEmailState.LoadState.Error) }
                    }
                }

                is AuthEmailContract.AuthEmailEvent.DeleteLateEmailCode -> {
                    val result = repository.deleteEmailCode(event.emailAuthId)
                    setState {
                        copy(
                            resetTimer = AuthEmailContract.AuthEmailState.ResetTimer(result),
                        )
                    }
                    currentState.countDownTimer.countDownTimer.cancel()
                }

                is AuthEmailContract.AuthEmailEvent.SendEmailAuth -> {
                    val result = repository.sendEmailAuth()
                    if (result != -1L) {
                        setState {
                            copy(
                                emailAuthId = AuthEmailContract.AuthEmailState.EmailAuthId(result),
                                countDownTimer = AuthEmailContract.AuthEmailState.CountDownTimer(
                                    setUpCountDownTimer()
                                ),
                                timeOver = AuthEmailContract.AuthEmailState.TimeOver(false)
                            )
                        }
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

    fun checkEmailCode(emailAuthId: Long, code: String, orgId: Long) {
        setEvent(AuthEmailContract.AuthEmailEvent.CheckEmailCode(emailAuthId, code, orgId))
    }

    fun deleteLateEmailCode(emailAuthId: Long) {
        setEvent(AuthEmailContract.AuthEmailEvent.DeleteLateEmailCode(emailAuthId))
    }

    fun sendEmailAuth() {
        setEvent(AuthEmailContract.AuthEmailEvent.SendEmailAuth)
    }
}