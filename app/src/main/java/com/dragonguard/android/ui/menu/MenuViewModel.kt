package com.dragonguard.android.ui.menu

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import kotlinx.coroutines.launch

class MenuViewModel :
    BaseViewModel<MenuContract.MenuEvent, MenuContract.MenuStates, MenuContract.MenuEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): MenuContract.MenuStates {
        pref = getPref()
        repository = getRepository()
        return MenuContract.MenuStates(
            MenuContract.MenuState.LoadState.Loading,
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