package com.dragonguard.android.ui.menu

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
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
                    val result = repository.checkAdmin()
                    setState { copy(admin = MenuContract.MenuState.AdminState(result)) }
                }

                is MenuContract.MenuEvent.WithDrawAccount -> {
                    val result = repository.withDrawAccount()
                    setState { copy(withDraw = MenuContract.MenuState.WithDrawState(result)) }
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