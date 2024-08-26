package com.dragonguard.android.ui.ranking

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class RankingsViewModel(private val repository: ApiRepository) :
    BaseViewModel<RankingsContract.RankingsEvent, RankingsContract.RankingsStates, RankingsContract.RankingsEffect>() {
    private lateinit var pref: IdPreference
    override fun createInitialState(): RankingsContract.RankingsStates {
        pref = getPref()
        return RankingsContract.RankingsStates(
            RankingsContract.RankingsState.LoadState.Initial,
            RankingsContract.RankingsState.Type(""),
            RankingsContract.RankingsState.Rankings.AllUsers.Ranking(arrayListOf()),
            RankingsContract.RankingsState.Rankings.AllUsers.Rankings(arrayListOf()),
            RankingsContract.RankingsState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: RankingsContract.RankingsEvent) {
        viewModelScope.launch {
            when (event) {
                is RankingsContract.RankingsEvent.GetTotalUserRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.getTotalUsersRankings(event.page, event.size, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.AllUsers.Ranking(
                                        rankings
                                    )
                                )
                            }
                        }
                }

                is RankingsContract.RankingsEvent.SetType -> {
                    setState { copy(type = RankingsContract.RankingsState.Type(event.type)) }
                }
            }
        }
    }

    fun setTypeName(type: String) {
        setEvent(RankingsContract.RankingsEvent.SetType(type))
    }

    fun getTotalUserRanking(page: Int, size: Int) {
        setEvent(RankingsContract.RankingsEvent.GetTotalUserRanking(page, size))
    }
}