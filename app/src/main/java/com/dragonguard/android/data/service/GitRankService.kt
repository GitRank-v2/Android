package com.dragonguard.android.data.service

import com.dragonguard.android.data.model.AuthStateModel
import com.dragonguard.android.data.model.GithubOrgReposModel
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.model.klip.TokenHistoryModel
import com.dragonguard.android.data.model.klip.WalletAddressModel
import com.dragonguard.android.data.model.klip.WalletAuthRequestModel
import com.dragonguard.android.data.model.klip.WalletAuthResponseModel
import com.dragonguard.android.data.model.klip.WalletAuthResultModel
import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.model.org.ApproveRequestOrgModel
import com.dragonguard.android.data.model.org.EmailAuthResultModel
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
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/*
 사용하는 모든 api의 네트워크 통신이 필요한 순간에 호출할 함수를 포함하는 인터페이스
 */
interface GitRankService {

    //    repo 검색 함수
    @GET("search")
    fun getRepoName(@QueryMap query: Map<String, String>): Call<RepoNameModel>

    @GET("search")
    fun getUserName(@QueryMap query: Map<String, String>): Call<UserNameModel>

    //    id에 해당하는 사용자의 정보를 받아오는 함수
    @GET("members/me")
    fun getUserInfo(): Call<UserInfoModel>

    @POST("members/me/update")
    fun userInfoUpdate(): Call<UserInfoModel>

    //    repoName에 해당하는 repo의 정보를 받아오는 함수
    @GET("git-repos")
    fun getRepoContributors(@Query("name") repoName: String): Call<RepoContributorsModel>

    @GET("git-repos/update")
    fun getRepoContributorsUpdate(@Query("name") repoName: String): Call<RepoContributorsModel>

    //    모든 사용자들의 랭킹을 받아오는 함수
    @GET("members/ranking")
    fun getTotalUsersRanking(@QueryMap query: Map<String, String>): Call<TotalUsersRankingModel>

    //    서버에 사용자의 활용도 최산화하는 함수
    @POST("members/contributions")
    fun postCommits(): Call<Unit>

    @GET("members/git-organizations/git-repos")
    fun getOrgRepoList(@Query("name") orgName: String): Call<GithubOrgReposModel>

    @GET("members/me/details")
    fun getMemberDetails(): Call<ClientDetailModel>

    //
    @POST("prepare")
    @Headers("accept: application/json", "content-type: application/json")
    fun postWalletAuth(@Body auth: WalletAuthRequestModel): Call<WalletAuthResponseModel>

    //    klip wallet address 정보제공동의 후 wallet address를 받아오는 함수
    @GET("result")
    fun getAuthResult(@Query("request_key") key: String): Call<WalletAuthResultModel>

    //    사용자의 토큰부여 내역을 가져오기 위한 함수
    @GET("blockchain")
    fun getTokenHistory(): Call<TokenHistoryModel>

    @POST("blockchain/update")
    fun updateToken(): Call<TokenHistoryModel>

    //    klip wallet address를 서버로 보내는 함수
    @POST("members/wallet-address")
    @Headers("accept: application/json", "content-type: application/json")
    fun postWalletAddress(@Body walletAddress: WalletAddressModel): Call<Unit>

    //    두 Repository의 구성원들의 정보를 받아오기 위한 함수
    @POST("git-repos/compare/git-repos-members")
    @Headers("accept: application/json", "content-type: application/json")
    fun postCompareRepoMembers(@Body compare: CompareRepoRequestModel): Call<CompareRepoMembersResponseModel>

    @POST("git-repos/compare/git-repos-members/update")
    @Headers("accept: application/json", "content-type: application/json")
    fun postCompareRepoMembersUpdate(@Body compare: CompareRepoRequestModel): Call<CompareRepoMembersResponseModel>

    //    두 Repository의 정보를 받아오기 위한 함수
    @POST("git-repos/compare")
    @Headers("accept: application/json", "content-type: application/json")
    fun postCompareRepo(@Body compare: CompareRepoRequestModel): Call<CompareRepoResponseModel>

    @POST("git-repos/compare")
    @Headers("accept: application/json", "content-type: application/json")
    fun postCompareRepoUpdate(@Body compare: CompareRepoRequestModel): Call<CompareRepoResponseModel>

    @GET("auth/refresh")
    fun getNewAccessToken(
        @Header("accessToken") access: String,
        @Header("refreshToken") refresh: String
    ): Call<RefreshTokenModel>

    @GET("organizations/search")
    fun getOrgNames(@QueryMap query: Map<String, String>): Call<OrganizationNamesModel>

    @POST("organizations")
    @Headers("accept: application/json", "content-type: application/json")
    fun postOrgRegist(@Body body: RegistOrgModel): Call<RegistOrgResultModel>

    @POST("organizations/add-member")
    @Headers("accept: application/json", "content-type: application/json")
    fun postAddOrgMember(@Body body: AddOrgMemberModel): Call<RegistOrgResultModel>

    @POST("email/send")
    @Headers("accept: application/json", "content-type: application/json")
    fun postAuthEmail(): Call<RegistOrgResultModel>

    @GET("email/check")
    fun getEmailAuthResult(@QueryMap query: Map<String, String>): Call<EmailAuthResultModel>

    @DELETE("email/{id}")
    fun deleteEmailCode(@Path("id") emailId: Long): Call<Unit>

    @GET("organizations/search-id")
    fun getOrgId(@Query("name") key: String): Call<RegistOrgResultModel>

    @GET("members/ranking/organization")
    fun getOrgInternalRankings(@QueryMap query: Map<String, String>): Call<OrgInternalRankingModel>

    @GET("organizations/ranking")
    fun getOrgRankings(@QueryMap query: Map<String, String>): Call<OrganizationRankingModel>

    @GET("organizations/ranking/all")
    fun getAllOrgRankings(@QueryMap query: Map<String, String>): Call<OrganizationRankingModel>


    @GET("admin/check")
    fun getPermissionState(): Call<Unit>

    @POST("admin/organizations/decide")
    fun postOrgApproval(@Body body: OrgApprovalModel): Call<ApproveRequestOrgModel>

    @GET("admin/organizations")
    fun getOrgStatus(@QueryMap query: Map<String, String>): Call<ApproveRequestOrgModel>

    @GET("members/details")
    fun getOthersProfile(@Query("githubId") query: String): Call<UserProfileModel>

    @GET("members/verify")
    fun getLoginAuthState(): Call<AuthStateModel>

    @DELETE("members/withdraw")
    fun postWithDraw(): Call<Unit>
}