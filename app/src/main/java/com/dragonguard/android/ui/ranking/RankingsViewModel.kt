package com.dragonguard.android.ui.ranking

import androidx.lifecycle.viewModelScope
import com.dragonguard.android.GitRankApplication.Companion.getPref
import com.dragonguard.android.GitRankApplication.Companion.getRepository
import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.data.repository.ApiRepository
import com.dragonguard.android.ui.base.BaseViewModel
import com.dragonguard.android.util.IdPreference
import kotlinx.coroutines.launch

class RankingsViewModel :
    BaseViewModel<RankingsContract.RankingsEvent, RankingsContract.RankingsStates, RankingsContract.RankingsEffect>() {
    private lateinit var pref: IdPreference
    private lateinit var repository: ApiRepository
    override fun createInitialState(): RankingsContract.RankingsStates {
        pref = getPref()
        repository = getRepository()
        return RankingsContract.RankingsStates(
            RankingsContract.RankingsState.LoadState.Initial,
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
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.getTotalUsersRankings(event.page, event.size, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.AllUsers.Ranking(
                                        baseRanking = rankings.map {
                                            TotalUsersRankingsModel(
                                                tokens = it.tokens,
                                                github_id = it.github_id,
                                                id = it.id,
                                                name = it.name,
                                                tier = it.tier,
                                                profile_image = it.profile_image
                                            )
                                        } as ArrayList<TotalUsersRankingsModel>
                                    )
                                )
                            }
                        }
                }

                is RankingsContract.RankingsEvent.GetTotalOrganizationRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.allOrgRanking(event.page, pref.getJwtToken("")).let { rankings ->
                        setState {
                            copy(
                                loadState = RankingsContract.RankingsState.LoadState.Success,
                                ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                    baseRanking = rankings.map {
                                        TotalOrganizationModel(
                                            email_endpoint = it.email_endpoint,
                                            id = it.id,
                                            name = it.name,
                                            organization_type = it.organization_type,
                                            token_sum = it.token_sum
                                        )
                                    } as ArrayList<TotalOrganizationModel>
                                )
                            )
                        }
                    }
                }

                is RankingsContract.RankingsEvent.GetCompanyRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.typeOrgRanking(COMPANY, event.page, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                        baseRanking = rankings.map {
                                            TotalOrganizationModel(
                                                email_endpoint = it.email_endpoint,
                                                id = it.id,
                                                name = it.name,
                                                organization_type = it.organization_type,
                                                token_sum = it.token_sum
                                            )
                                        } as ArrayList<TotalOrganizationModel>
                                    )
                                )
                            }
                        }
                }

                is RankingsContract.RankingsEvent.GetUniversityRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.typeOrgRanking(UNIVERSITY, event.page, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                        baseRanking = rankings.map {
                                            TotalOrganizationModel(
                                                email_endpoint = it.email_endpoint,
                                                id = it.id,
                                                name = it.name,
                                                organization_type = it.organization_type,
                                                token_sum = it.token_sum
                                            )
                                        } as ArrayList<TotalOrganizationModel>
                                    )
                                )
                            }
                        }
                }

                is RankingsContract.RankingsEvent.GetHighSchoolRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.typeOrgRanking(HIGH_SCHOOL, event.page, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                        baseRanking = rankings.map {
                                            TotalOrganizationModel(
                                                email_endpoint = it.email_endpoint,
                                                id = it.id,
                                                name = it.name,
                                                organization_type = it.organization_type,
                                                token_sum = it.token_sum
                                            )
                                        } as ArrayList<TotalOrganizationModel>
                                    )
                                )
                            }
                        }
                }

                is RankingsContract.RankingsEvent.GetEtcRanking -> {
                    setState { copy(loadState = RankingsContract.RankingsState.LoadState.Loading) }
                    repository.typeOrgRanking(ETC, event.page, pref.getJwtToken(""))
                        .let { rankings ->
                            setState {
                                copy(
                                    loadState = RankingsContract.RankingsState.LoadState.Success,
                                    ranking = RankingsContract.RankingsState.Rankings.Organization.Ranking(
                                        baseRanking = rankings.map {
                                            TotalOrganizationModel(
                                                email_endpoint = it.email_endpoint,
                                                id = it.id,
                                                name = it.name,
                                                organization_type = it.organization_type,
                                                token_sum = it.token_sum
                                            )
                                        } as ArrayList<TotalOrganizationModel>
                                    )
                                )
                            }
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

    companion object {
        private const val COMPANY = "COMPANY"
        private const val UNIVERSITY = "UNIVERSITY"
        private const val HIGH_SCHOOL = "HIGH_SCHOOL"
        private const val ETC = "ETC"
    }
}