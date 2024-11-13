package com.dragonguard.android.ui.menu.org.auth

import androidx.lifecycle.MutableLiveData
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState
import com.dragonguard.android.util.LoadState
import kotlinx.coroutines.Job

class AuthEmailContract {
    sealed class AuthEmailEvent : UiEvent {
        data class RequestAuthEmail(val orgId: Long, val email: String) : AuthEmailEvent()
        data class CheckEmailCode(val emailAuthId: Long, val code: String, val orgId: Long) :
            AuthEmailEvent()

        data class DeleteLateEmailCode(val emailAuthId: Long) : AuthEmailEvent()
        data object SendEmailAuth : AuthEmailEvent()
    }

    sealed class AuthEmailState {
        data class Timer(var oldTime: Long, val timer: Job) : AuthEmailState()
        data class CustomTimerDuration(val timerDuration: MutableLiveData<Long>) : AuthEmailState()
        data class EmailAuthId(val emailAuthId: Long) : AuthEmailState()
        data class ResetTimer(val reset: Boolean) : AuthEmailState()
        data class CountDownTimer(var countDownTimer: android.os.CountDownTimer) : AuthEmailState()
        data class RemainTime(val remainTime: String) : AuthEmailState()
        data class TimeOver(val timeOver: Boolean) : AuthEmailState()
    }

    data class AuthEmailStates(
        val state: LoadState,
        val customTimerDuration: AuthEmailState.CustomTimerDuration,
        val timer: AuthEmailState.Timer,
        val countDownTimer: AuthEmailState.CountDownTimer,
        val emailAuthId: AuthEmailState.EmailAuthId,
        val resetTimer: AuthEmailState.ResetTimer,
        val remainTime: AuthEmailState.RemainTime,
        val timeOver: AuthEmailState.TimeOver
    ) : UiState

    sealed class AuthEmailEffect : UiEffect {

    }
}