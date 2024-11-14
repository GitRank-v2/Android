package com.dragonguard.android.ui.menu

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val pref: IdPreference,
    private val repository: ApiRepository
) : BaseViewModel<MenuContract.MenuEvent, MenuContract.MenuStates, MenuContract.MenuEffect>() {
    override fun createInitialState(): MenuContract.MenuStates {
        return MenuContract.MenuStates(
            MenuContract.MenuState.AdminState(false),
            MenuContract.MenuState.WithDrawState(false),
            MenuContract.MenuState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: MenuContract.MenuEvent) {
        viewModelScope.launch {
            when (event) {
                is MenuContract.MenuEvent.CheckAdmin -> {
                    repository.checkAdmin().onSuccess {
                        setState { copy(admin = MenuContract.MenuState.AdminState(it)) }
                    }.onFail {

                    }

                }

                is MenuContract.MenuEvent.WithDrawAccount -> {
                    repository.withDrawAccount().onSuccess {
                        setState { copy(withDraw = MenuContract.MenuState.WithDrawState(it)) }
                    }.onError {

                    }
                }
            }
        }
    }

    fun checkAdmin() {
        setEvent(MenuContract.MenuEvent.CheckAdmin)
    }

    fun withDrawAccount() {
        setEvent(MenuContract.MenuEvent.WithDrawAccount)
    }
}