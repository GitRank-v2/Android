package com.dragonguard.android.ui.ranking

import com.dragonguard.android.data.model.rankings.OrganizationRankingModelItem
import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModelItem
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class RankingsContract {
    sealed class RankingsEvent : UiEvent {
        data class GetTotalUserRanking(val page: Int, val size: Int) : RankingsEvent()
        data class SetType(val type: String) : RankingsEvent()
    }

    sealed class RankingsState {
        sealed class LoadState : RankingsState() {
            object Initial : LoadState()
            object Loading : LoadState()
            object Success : LoadState()
            object Error : LoadState()
        }

        data class Type(val type: String) : RankingsState()
        sealed class Rankings(val ranking: ArrayList<*>) : RankingsState() {
            sealed class AllUsers(val userRanking: ArrayList<TotalUsersRankingsModel>) :
                Rankings(userRanking) {
                data class Ranking(val baseRanking: ArrayList<TotalUsersRankingsModel>) :
                    AllUsers(baseRanking)

                data class Rankings(val rankings: ArrayList<TotalUsersRankingsModel>) :
                    AllUsers(rankings)
            }

            sealed class Organization(val orgRanking: ArrayList<TotalOrganizationModel>) : Rankings(orgRanking) {
                data class Ranking(val baseRanking: ArrayList<TotalOrganizationModel>) :
                    Organization(baseRanking)

                data class Rankings(val rankings: ArrayList<TotalOrganizationModel>) :
                    Organization(rankings)
            }


        }

        data class Token(val token: String) : RankingsState()
    }

    data class RankingsStates(
        val loadState: RankingsState.LoadState,
        val type: RankingsState.Type,
        val ranking: RankingsState.Rankings,
        val rankings: RankingsState.Rankings,
        val token: RankingsState.Token
    ) : UiState

    sealed class RankingsEffect : UiEffect {

    }

}