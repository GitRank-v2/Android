package com.dragonguard.android.ui.ranking.outer

import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.ui.base.UiEffect
import com.dragonguard.android.ui.base.UiEvent
import com.dragonguard.android.ui.base.UiState

class RankingsContract {
    sealed class RankingsEvent : UiEvent {
        data class SetType(val type: String) : RankingsEvent()
        data object SetTypeToUser : RankingsEvent()
        data class GetTotalUserRanking(val page: Int, val size: Int) : RankingsEvent()
        data class GetTotalOrganizationRanking(val page: Int) : RankingsEvent()
        data class GetCompanyRanking(val page: Int) : RankingsEvent()
        data class GetUniversityRanking(val page: Int) : RankingsEvent()
        data class GetHighSchoolRanking(val page: Int) : RankingsEvent()
        data class GetEtcRanking(val page: Int) : RankingsEvent()
    }

    sealed class RankingsState {
        sealed class LoadState : RankingsState() {
            data object Initial : LoadState()
            data object Loading : LoadState()
            data object Success : LoadState()
            data object Error : LoadState()
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

            sealed class Organization(val orgRanking: ArrayList<TotalOrganizationModel>) :
                Rankings(orgRanking) {
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