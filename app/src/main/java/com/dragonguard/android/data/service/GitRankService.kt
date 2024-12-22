package com.dragonguard.android.data.service

import com.dragonguard.android.data.model.AuthStateModel
import com.dragonguard.android.data.model.StandardResponse
import com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.data.model.compare.CompareRepoRequestModel
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.data.model.detail.ClientDetailModel
import com.dragonguard.android.data.model.detail.GitOrganization
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.data.model.main.UserContributionsModel
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.data.model.org.AddOrgMemberModel
import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.data.model.org.EmailAuthResultModel
import com.dragonguard.android.data.model.org.OrgApprovalModel
import com.dragonguard.android.data.model.org.OrganizationNamesModelItem
import com.dragonguard.android.data.model.org.RegistOrgModel
import com.dragonguard.android.data.model.org.RegistOrgResultModel
import com.dragonguard.android.data.model.profile.GithubOrgReposModel
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModelItem
import com.dragonguard.android.data.model.rankings.OrganizationRankingModelItem
import com.dragonguard.android.data.model.rankings.TotalUsersRankingModelItem
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.data.model.token.RefreshTokenModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface GitRankService {
    // repo 검색 요청
    @GET("search")
    suspend fun getRepoName(@QueryMap query: Map<String, String>): Response<StandardResponse<List<RepoSearchResultModel>>>

    // 사용자 검색 요청
    @GET("search")
    suspend fun getUserName(@QueryMap query: Map<String, String>): Response<StandardResponse<List<UserNameModelItem>>>

    //    id에 해당하는 사용자의 정보를 요청
    @GET("members/me")
    suspend fun getUserInfo(): Response<StandardResponse<UserInfoModel>>

    //    repoName에 해당하는 repo의 정보를 요청
    @GET("git-repos")
    suspend fun getRepoContributors(@Query("name") repoName: String): Response<StandardResponse<RepoContributorsModel>>

    @POST("git-repos/update")
    suspend fun getRepoContributorsUpdate(@Query("name") repoName: String): Response<StandardResponse<RepoContributorsModel>>

    //    모든 사용자들의 랭킹을 요청
    @GET("ranks/members")
    suspend fun getTotalUsersRanking(@QueryMap query: Map<String, String>): Response<StandardResponse<List<TotalUsersRankingModelItem>>>

    //    서버에 사용자의 활용도 최산화를 요청
    @POST("members/contributions")
    suspend fun updateGitContribution(): Response<Unit>

    @GET("members/contributions")
    suspend fun updateGitContributions(): Response<StandardResponse<List<UserContributionsModel>>>

    @GET("git-repos/git-organizations")
    suspend fun getOrgRepoList(@Query("name") orgName: String): Response<StandardResponse<GithubOrgReposModel>>

    @GET("members/me/git-details")
    suspend fun getMemberDetails(): Response<StandardResponse<ClientDetailModel>>

    @GET("git-repos/me")
    suspend fun getClientRepository(): Response<StandardResponse<List<String>>>

    @GET("git-orgs/me")
    suspend fun getClientOrganization(): Response<StandardResponse<List<GitOrganization>>>

    //    두 Repository의 구성원들의 정보를 받아오기 위한 함수
    @POST("git-repos/compare/git-repos-members")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun postCompareRepoMembers(@Body compare: CompareRepoRequestModel): Response<StandardResponse<CompareRepoMembersResponseModel>>

    //    두 Repository의 정보를 받아오기 위한 함수
    @POST("git-repos/compare")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun postCompareRepo(@Body compare: CompareRepoRequestModel): Response<StandardResponse<CompareRepoResponseModel>>


    @GET("auth/refresh")
    suspend fun getNewAccessToken(
        @Header("Access") access: String,
        @Header("Refresh") refresh: String
    ): Response<StandardResponse<RefreshTokenModel>>

    @GET("organizations/search")
    suspend fun searchOrgNamesByName(@QueryMap query: Map<String, String>): Response<StandardResponse<List<OrganizationNamesModelItem>>>

    @GET("organizations")
    suspend fun searchOrgNames(@QueryMap query: Map<String, String>): Response<StandardResponse<List<OrganizationNamesModelItem>>>

    @POST("organizations")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun postOrgRegist(@Body body: RegistOrgModel): Response<StandardResponse<RegistOrgResultModel>>

    @POST("organizations/join")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun postAddOrgMember(@Body body: AddOrgMemberModel): Response<StandardResponse<RegistOrgResultModel>>

    @POST("email/{id}/resend")
    @Headers("accept: application/json", "content-type: application/json")
    suspend fun postResendEmail(@Path("id") emailId: Long): Response<StandardResponse<RegistOrgResultModel>>

    @POST("email/{id}/check")
    suspend fun getEmailAuthResult(
        @Path("id") id: String,
        @QueryMap query: Map<String, String>
    ): Response<StandardResponse<EmailAuthResultModel>>

    @DELETE("email/{id}")
    suspend fun deleteEmailCode(@Path("id") emailId: Long): Response<Unit>

    @GET("organizations/id")
    suspend fun getOrgId(@Query("name") key: String): Response<StandardResponse<RegistOrgResultModel>>

    @GET("ranks/organizations/{id}/members")
    suspend fun getOrgInternalRankings(
        @Path("id") id: String,
        @QueryMap query: Map<String, String>
    ): Response<StandardResponse<List<OrgInternalRankingModelItem>>>

    @GET("ranks/organizations")
    suspend fun getOrgRankings(@QueryMap query: Map<String, String>): Response<StandardResponse<List<OrganizationRankingModelItem>>>

    @GET("ranks/organizations/all")
    suspend fun getAllOrgRankings(@QueryMap query: Map<String, String>): Response<StandardResponse<List<OrganizationRankingModelItem>>>

    @GET("admin/check")
    suspend fun getPermissionState(): Response<Unit>

    @POST("admin/organizations/{id}/decide")
    suspend fun postOrgApproval(
        @Path("id") emailId: Long,
        @Body body: OrgApprovalModel
    ): Response<StandardResponse<List<ApproveRequestOrgModelItem>>>

    @GET("admin/organizations")
    suspend fun getOrgStatus(@QueryMap query: Map<String, String>): Response<StandardResponse<List<ApproveRequestOrgModelItem>>>

    @GET("members/details/me")
    suspend fun getUserProfile(): Response<StandardResponse<UserProfileModel>>

    @GET("members/details")
    suspend fun getOthersProfile(@Query("githubId") query: String): Response<StandardResponse<UserProfileModel>>

    @GET("members/verify")
    suspend fun getLoginAuthState(): Response<StandardResponse<AuthStateModel>>

    @DELETE("members")
    suspend fun postWithDraw(): Response<Unit>
}