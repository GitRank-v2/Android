package com.dragonguard.android.ui.ranking.outer

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.data.repository.ranking.outer.RankingsRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.onError
import com.dragonguard.android.util.onException
import com.dragonguard.android.util.onFail
import com.dragonguard.android.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val repository: RankingsRepository
) : BaseViewModel<RankingsContract.RankingsEvent, RankingsContract.RankingsStates, RankingsContract.RankingsEffect>() {
    private lateinit var pref: IdPreference

    override fun createInitialState(): RankingsContract.RankingsStates {
        pref = getPref()
        return RankingsContract.RankingsStates(
            LoadState.INIT,
            RankingsContract.RankingsState.Type(""),
            RankingsContract.RankingsState.Rankings.Organization.Ranking(arrayListOf()),
            RankingsContract.RankingsState.Rankings.Organization.Rankings(arrayListOf()),
            RankingsContract.RankingsState.Token(pref.getJwtToken(""))
        )
    }

    override fun handleEvent(event: RankingsContract.RankingsEvent) {
        viewModelScope.launch {
            when (event) {
                is RankingsContract.RankingsEvent.GetTotalUserRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.getTotalUsersRankings(event.page, event.size).onSuccess {
                        Log.d("user ranking", "success $it")
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.AllUsers.Ranking(
                                    baseRanking = it.map { info ->
                                        TotalUsersRankingsModel(
                                            contribution_amount = info.contribution_amount,
                                            github_id = info.github_id,
                                            id = info.id,
                                            tier = info.tier,
                                            profile_image = info.profile_image
                                        )
                                    } as ArrayList<TotalUsersRankingsModel>
                                )
                            )
                        }
                    }.onFail {
                        Log.d("RankingsViewModel", "handleEvent: $it")
                    }.onException {
                        Log.d("RankingsViewModel", "handleEvent: $it")
                    }.onError {
                        Log.d("RankingsViewModel", "handleEvent: $it")
                    }
                }

                is RankingsContract.RankingsEvent.GetTotalOrganizationRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.allOrgRanking(event.page).onSuccess {
                        Log.d("org ranking", "success $it")
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = it.map {
                                        TotalOrganizationModel(
                                            id = it.id,
                                            name = it.name,
                                            contribution_amount = it.contribution_amount,
                                            type = it.type
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }.onError {
                        Log.d("RankingsViewModel", "handleEvent: ${it}")
                    }
                }

                is RankingsContract.RankingsEvent.GetCompanyRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.typeOrgRanking(COMPANY, event.page).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = it.map {
                                        TotalOrganizationModel(
                                            id = it.id,
                                            name = it.name,
                                            contribution_amount = it.contribution_amount,
                                            type = it.type
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is RankingsContract.RankingsEvent.GetUniversityRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.typeOrgRanking(UNIVERSITY, event.page).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = it.map { org ->
                                        TotalOrganizationModel(
                                            id = org.id,
                                            name = org.name,
                                            contribution_amount = org.contribution_amount,
                                            type = org.type
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is RankingsContract.RankingsEvent.GetHighSchoolRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.typeOrgRanking(HIGH_SCHOOL, event.page).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = it.map { org ->
                                        TotalOrganizationModel(
                                            id = org.id,
                                            name = org.name,
                                            contribution_amount = org.contribution_amount,
                                            type = org.type
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is RankingsContract.RankingsEvent.GetEtcRanking -> {
                    setState { copy(loadState = LoadState.LOADING) }
                    repository.typeOrgRanking(ETC, event.page).onSuccess {
                        setState {
                            copy(
                                loadState = LoadState.SUCCESS,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = it.map { org ->
                                        TotalOrganizationModel(
                                            id = org.id,
                                            name = org.name,
                                            contribution_amount = org.contribution_amount,
                                            type = org.type
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }.onFail {

                    }
                }

                is RankingsContract.RankingsEvent.SetTypeToUser -> {
                    setState {
                        copy(
                            ranking = RankingsContract.RankingsState.Rankings.AllUsers.Ranking(
                                arrayListOf()
                            ),
                            rankings = RankingsContract.RankingsState.Rankings.AllUsers.Rankings(
                                arrayListOf()
                            ),
                        )
                    }
                }

                is RankingsContract.RankingsEvent.SetType -> {
                    setState { copy(type = RankingsContract.RankingsState.Type(event.type)) }
                }

                is RankingsContract.RankingsEvent.AddUserRanking -> {
                    setState {
                        copy(
                            rankings = RankingsContract.RankingsState.Rankings.AllUsers.Rankings(
                                (rankings.ranking + ranking.ranking) as ArrayList<TotalUsersRankingsModel>
                            )
                        )
                    }
                }

                is RankingsContract.RankingsEvent.AddOrganizationRanking -> {
                    setState {
                        copy(
                            rankings = RankingsContract.RankingsState.Rankings.Organization.Rankings(
                                (rankings.ranking + ranking.ranking) as ArrayList<TotalOrganizationModel>
                            )
                        )
                    }
                }

            }
        }
    }

    fun setTypeName(type: String) {
        setEvent(RankingsContract.RankingsEvent.SetType(type))
    }

    fun setTypeToUser() {
        setEvent(RankingsContract.RankingsEvent.SetTypeToUser)
    }

    fun getTotalUserRanking(page: Int, size: Int) {
        setEvent(RankingsContract.RankingsEvent.GetTotalUserRanking(page, size))
    }

    fun getTotalOrganizationRanking(page: Int) {
        setEvent(RankingsContract.RankingsEvent.GetTotalOrganizationRanking(page))
    }

    fun getCompanyRanking(page: Int) {
        setEvent(RankingsContract.RankingsEvent.GetCompanyRanking(page))
    }

    fun getUniversityRanking(page: Int) {
        setEvent(RankingsContract.RankingsEvent.GetUniversityRanking(page))
    }

    fun getHighSchoolRanking(page: Int) {
        setEvent(RankingsContract.RankingsEvent.GetHighSchoolRanking(page))
    }

    fun getEtcRanking(page: Int) {
        setEvent(RankingsContract.RankingsEvent.GetEtcRanking(page))
    }

    fun addUserRanking() {
        setEvent(RankingsContract.RankingsEvent.AddUserRanking)
    }

    fun addOrganizationRanking() {
        setEvent(RankingsContract.RankingsEvent.AddOrganizationRanking)
    }

    companion object {
        private const val COMPANY = "COMPANY"
        private const val UNIVERSITY = "UNIVERSITY"
        private const val HIGH_SCHOOL = "HIGH_SCHOOL"
        private const val ETC = "ETC"
    }
}