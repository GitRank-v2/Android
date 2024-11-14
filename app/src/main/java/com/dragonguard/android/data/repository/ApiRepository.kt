package com.dragonguard.android.data.repository

import android.util.Log
import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.data.model.rankings.OrganizationRankingModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModel
import com.dragonguard.android.data.model.search.RepoNameModel
import com.dragonguard.android.data.model.search.UserNameModel
import com.dragonguard.android.data.model.token.RefreshTokenModel
import com.dragonguard.android.data.service.GitRankService
import com.dragonguard.android.util.DataResult
import com.dragonguard.android.util.handleAdminApi
import com.dragonguard.android.util.handleApi
import com.dragonguard.android.util.handleLoginApi
import com.dragonguard.android.util.handleWithdrawApi
import javax.inject.Inject

/*
 서버에 요청하는 모든 api들의 호출부분
 */
class ApiRepository @Inject constructor(private val service: GitRankService) {


    //Repository 검색을 위한 함수
    suspend fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
    ): DataResult<RepoNameModel> {

        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        return handleApi({ service.getRepoName(queryMap) }) { it }

    }

    suspend fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
    ): DataResult<RepoNameModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("filters", filters)
        Log.d("api 호출", "이름: $name, type: $type filters: $filters")
        Log.d("api 호출", "$count 페이지 검색")
        return handleApi({ service.getRepoName(queryMap) }) { it }
    }

    suspend fun getUserNames(name: String, count: Int, type: String): DataResult<UserNameModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        return handleApi({ service.getUserName(queryMap) }) { it }
    }

    //사용자의 정보를 받아오기 위한 함수
    suspend fun getUserInfo(): DataResult<UserInfoModel> {
        return handleApi({ service.getUserInfo() }) { it }
    }

    suspend fun getClientDetails(): DataResult<ClientDetailModel> {
        return handleApi({ service.getMemberDetails() }) { it }
    }

    //Repository의 기여자들의 정보를 받아오기 위한 함수
    suspend fun getRepoContributors(repoName: String): DataResult<RepoContributorsModel> {
        return handleApi({ service.getRepoContributors(repoName) }) { it }
    }

    //klip wallet을 등록한 모든 사용자의 토큰에 따른 등수를 받아오는 함수
    suspend fun getTotalUsersRankings(
        page: Int,
        size: Int
    ): DataResult<TotalUsersRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$page")
        queryMap.put("size", "$size")
        queryMap.put("sort", "tokens,DESC")
        return handleApi({ service.getTotalUsersRanking(queryMap) }) { it }
    }

    //사용자의 토큰 부여 내역을 확인하기 위한 함수
    /*fun getTokenHistory(): ArrayList<TokenHistoryModelItem>? {
        val tokenHistory = service.getTokenHistory()
        var tokenHistoryResult: ArrayList<TokenHistoryModelItem>? = null
        try {
            val result = tokenHistory.execute()
            Log.d("result", "토큰 부여 내역 결과 ${result.code()}")
            tokenHistoryResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "토큰 부여 내역 결과 ${e.message}")
            return null
        }
        return tokenHistoryResult
    }*/

    //klip wallet address를 서버에 등록하기 위한 함수
    /*fun postWalletAddress(body: WalletAddressModel): Boolean {
        val walletAddress = service.postWalletAddress(body)
        return try {
            val result = walletAddress.execute()
            Log.d("dd", "지갑주소 전송 결과 : ${result.code()} ${body.wallet_address}")
            result.isSuccessful
        } catch (e: Exception) {
            Log.d("dd", "결과 실패")
            false
        }
    }*/

    /*
    //kilp wallet address의 정보제공을 위한 함수
    fun postWalletAuth(body: WalletAuthRequestModel): WalletAuthResponseModel {
        var authResult = WalletAuthResponseModel(null, null, null)
        val retrofitWallet = Retrofit.Builder().baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val apiWallet = retrofitWallet.create(GitRankService::class.java)
        val authWallet = apiWallet.postWalletAuth(body)
        try {
            val result = authWallet.execute()
            Log.d("result", "지갑주소 인증 결과 ${result.code()}")
            authResult = result.body()!!
        } catch (e: Exception) {
            Log.d("result", "지갑주소 인증 실패 ${e.message}")
            return authResult
        }
        return authResult
    }

    //klip wallet address 정보제공동의 결과를 받아오는 함수
    fun getAuthResult(key: String): WalletAuthResultModel {
        var authResult = WalletAuthResultModel(null, null, null, null)
        val retrofitWallet = Retrofit.Builder().baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val apiWallet = retrofitWallet.create(GitRankService::class.java)
        val authWallet = apiWallet.getAuthResult(key)
        try {
            val result = authWallet.execute()
            Log.d("result", "klip 인증 결과 ${result.code()}")
            authResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "klip 인증 오류 ${e.message}")
            return authResult
        }
        return authResult
    }
    */

    //두 Repository의 구성원들의 기여도를 받아오기 위한 함수
    suspend fun postCompareRepoMembersRequest(body: CompareRepoRequestModel): DataResult<CompareRepoMembersResponseModel> {
        return handleApi({ service.postCompareRepoMembers(body) }) { it }
    }

    //두 Repository의 정보를 받아오기 위한 함수
    suspend fun postCompareRepoRequest(body: CompareRepoRequestModel): DataResult<CompareRepoResponseModel> {
        return handleApi({ service.postCompareRepo(body) }) { it }
    }

    suspend fun getNewAccessToken(access: String, refresh: String): DataResult<RefreshTokenModel> {
        return handleApi({ service.getNewAccessToken(access, refresh) }) { it }
    }

    suspend fun updateGitContributions(): DataResult<Unit> {
        return handleApi({ service.updateGitContributions() }) { it }
    }

    suspend fun getOrgNames(
        name: String,
        count: Int,
        type: String
    ): DataResult<OrganizationNamesModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$count")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("size", "10")
        return handleApi({ service.getOrgNames(queryMap) }) { it }
    }

    suspend fun postRegistOrg(body: RegistOrgModel): DataResult<RegistOrgResultModel> {
        return handleApi({ service.postOrgRegist(body) }) { it }
    }

    suspend fun addOrgMember(body: AddOrgMemberModel): DataResult<Long> {
        return handleApi({ service.postAddOrgMember(body) }) { it.id }
    }

    suspend fun sendEmailAuth(): DataResult<Long> {
        return handleApi({ service.postAuthEmail() }) { it.id }
    }

    suspend fun emailAuthResult(id: Long, code: String, orgId: Long): DataResult<Boolean> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("id", id.toString())
        queryMap.put("code", code)
        queryMap.put("organizationId", orgId.toString())
        return handleApi({ service.getEmailAuthResult(queryMap) }) { it.is_valid_code }
    }

    suspend fun deleteEmailCode(id: Long): DataResult<Unit> {
        return handleApi({ service.deleteEmailCode(id) }) { it }
    }

    suspend fun searchOrgId(orgName: String): DataResult<Long> {
        return handleApi({ service.getOrgId(orgName) }) { it.id }
    }

    suspend fun orgInternalRankings(id: Long, count: Int): DataResult<OrgInternalRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("organizationId", id.toString())
        queryMap.put("page", count.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgInternalRankings(queryMap) }) { it }
    }

    suspend fun typeOrgRanking(type: String, page: Int): DataResult<OrganizationRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("type", type)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgRankings(queryMap) }) { it }
    }

    suspend fun allOrgRanking(page: Int): DataResult<OrganizationRankingModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getAllOrgRankings(queryMap) }) { it }
    }


    suspend fun checkAdmin(): DataResult<Boolean> {
        return handleAdminApi { service.getPermissionState() }

    }

    suspend fun approveOrgRequest(body: OrgApprovalModel): DataResult<ApproveRequestOrgModel> {
        return handleApi({ service.postOrgApproval(body) }) { it }
    }

    suspend fun statusOrgList(status: String, page: Int): DataResult<ApproveRequestOrgModel> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("status", status)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        return handleApi({ service.getOrgStatus(queryMap) }) { it }
    }

    suspend fun userGitOrgRepoList(orgName: String): DataResult<GithubOrgReposModel> {
        return handleApi({ service.getOrgRepoList(orgName) }) { it }
    }

    suspend fun otherProfile(githubId: String): DataResult<UserProfileModel> {
        return handleApi({ service.getOthersProfile(githubId) }) { it }
    }

    suspend fun manualCompareMembers(body: CompareRepoRequestModel): DataResult<CompareRepoMembersResponseModel> {
        return handleApi({ service.postCompareRepoMembersUpdate(body) }) { it }
    }

    suspend fun manualCompareRepo(body: CompareRepoRequestModel): DataResult<CompareRepoResponseModel> {
        return handleApi({ service.postCompareRepoUpdate(body) }) { it }
    }

    suspend fun manualContribute(repoName: String): DataResult<RepoContributorsModel> {
        return handleApi({ service.getRepoContributorsUpdate(repoName) }) { it }
    }


    suspend fun manualUserInfo(): DataResult<UserInfoModel> {
        return handleApi({ service.userInfoUpdate() }) { it }
    }

    suspend fun getLoginState(): DataResult<Boolean?> {
        return handleLoginApi { service.getLoginAuthState() }
    }

    suspend fun withDrawAccount(): DataResult<Boolean> {
        return handleWithdrawApi { service.postWithDraw() }

    }
}