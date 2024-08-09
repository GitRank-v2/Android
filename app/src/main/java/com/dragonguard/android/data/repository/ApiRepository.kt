package com.dragonguard.android.data.repository

import android.util.Log
import com.dragonguard.android.data.service.GitRankService
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/*
 서버에 요청하는 모든 api들의 호출부분
 */
class ApiRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(18, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl("")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(GitRankService::class.java)

    //Repository 검색을 위한 함수
    fun getRepositoryNames(
        name: String,
        count: Int,
        type: String,
        token: String
    ): ArrayList<com.dragonguard.android.data.model.search.RepoSearchResultModel> {
        var repoNames: ArrayList<com.dragonguard.android.data.model.search.RepoSearchResultModel> =
            arrayListOf<com.dragonguard.android.data.model.search.RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)

        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap, "Bearer $token")
        try {
            val result = repoName.execute()
            Log.d("result", "레포 필터없는 검색 결과: ${result.code()}}")
            repoNames = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 필터없는 검색 error: ${e.message}")
            return repoNames
        }
        return repoNames
    }

    fun getRepositoryNamesWithFilters(
        name: String,
        count: Int,
        filters: String,
        type: String,
        token: String
    ): ArrayList<com.dragonguard.android.data.model.search.RepoSearchResultModel> {
        var repoNames: ArrayList<com.dragonguard.android.data.model.search.RepoSearchResultModel> =
            arrayListOf<com.dragonguard.android.data.model.search.RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("filters", filters)
        Log.d("api 호출", "이름: $name, type: $type filters: $filters")
        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap, "Bearer $token")
        try {
            val result = repoName.execute()
            Log.d("result", "레포 필터별 검색: ${result.code()}")
            repoNames = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 필터별 검색: ${e.message}")
            return repoNames
        }
        return repoNames
    }

    fun getUserNames(
        name: String,
        count: Int,
        type: String,
        token: String
    ): ArrayList<com.dragonguard.android.data.model.search.UserNameModelItem> {
        var repoNames: ArrayList<com.dragonguard.android.data.model.search.UserNameModelItem> =
            arrayListOf<com.dragonguard.android.data.model.search.UserNameModelItem>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "${count + 1}")
        queryMap.put("name", name)
        queryMap.put("type", type)

        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getUserName(queryMap, "Bearer $token")
        try {
            val result = repoName.execute()
            Log.d("result", "사용자 검색 결과: ${result.code()}")
            repoNames = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "사용자 검색 error: ${e.message}")
            return repoNames
        }
        return repoNames
    }

    //사용자의 정보를 받아오기 위한 함수
    fun getUserInfo(token: String): com.dragonguard.android.data.model.UserInfoModel {
        val userInfo = api.getUserInfo("Bearer $token")
        var userResult = com.dragonguard.android.data.model.UserInfoModel(
            null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null
        )
        try {
            val result = userInfo.execute()
            Log.d("error", "사용자 정보 요청 주소 : ${userInfo.request().url}")
            Log.d("result", "사용자 정보 요청 결과 : ${result.code()}")
            userResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "사용자 정보 요청 에러 : ${e.message}")
            return userResult
        }
        return userResult
    }

    fun getClientDetails(token: String): com.dragonguard.android.data.model.detail.ClientDetailModel? {
        val userDetails = api.getMemberDetails("Bearer $token")
        return try {
            val result = userDetails.execute()
            Log.d("error", "client 정보 요청 주소 : ${userDetails.request().url}")
            Log.d("result", "client 정보 요청 결과 : ${result.code()}")
            result.body()!!
        } catch (e: Exception) {
            Log.d("error", "client 정보 요청 에러 : ${e.message}")
            return null
        }
    }

    //Repository의 기여자들의 정보를 받아오기 위한 함수
    fun getRepoContributors(
        repoName: String,
        token: String
    ): com.dragonguard.android.data.model.contributors.RepoContributorsModel? {
        val repoContributors = api.getRepoContributors(repoName, "Bearer $token")
        var repoContResult =
            com.dragonguard.android.data.model.contributors.RepoContributorsModel(null, null)
        try {
            val result = repoContributors.execute()
//            if(result.code() == 204) {
//                Log.d("error", "레포 상세조회 결과 ${result.code()}")
//                return null
//            }
            Log.d("error", "레포 상세조회 결과 ${result.code()}")
            repoContResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 상세조회 에러 ${e.message}")
            return repoContResult
        }
        return repoContResult
    }

    //klip wallet을 등록한 모든 사용자의 토큰에 따른 등수를 받아오는 함수
    fun getTotalUsersRankings(
        page: Int,
        size: Int,
        token: String
    ): ArrayList<com.dragonguard.android.data.model.rankings.TotalUsersRankingModelItem> {
        var rankingResult =
            ArrayList<com.dragonguard.android.data.model.rankings.TotalUsersRankingModelItem>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$page")
        queryMap.put("size", "$size")
        queryMap.put("sort", "tokens,DESC")
        val ranking = api.getTotalUsersRanking(queryMap, "Bearer $token")
        try {
            val result = ranking.execute()
            Log.d("result", "유저랭킹 api 결과 ${result.code()}")
            rankingResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "유저랭킹 api 에러 ${e.message}")
            return rankingResult
        }
        return rankingResult
    }

    //사용자의 토큰 부여 내역을 확인하기 위한 함수
    fun getTokenHistory(token: String): ArrayList<com.dragonguard.android.data.model.klip.TokenHistoryModelItem>? {
        val tokenHistory = api.getTokenHistory("Bearer $token")
        var tokenHistoryResult: ArrayList<com.dragonguard.android.data.model.klip.TokenHistoryModelItem>? =
            null
        try {
            val result = tokenHistory.execute()
            Log.d("result", "토큰 부여 내역 결과 ${result.code()}")
            tokenHistoryResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "토큰 부여 내역 결과 ${e.message}")
            return null
        }
        return tokenHistoryResult
    }

    //klip wallet address를 서버에 등록하기 위한 함수
    fun postWalletAddress(
        body: com.dragonguard.android.data.model.klip.WalletAddressModel,
        token: String
    ): Boolean {
        val walletAddress = api.postWalletAddress(body, "Bearer $token")
        return try {
            val result = walletAddress.execute()
            Log.d("dd", "지갑주소 전송 결과 : ${result.code()} ${body.wallet_address}")
            result.isSuccessful
        } catch (e: Exception) {
            Log.d("dd", "결과 실패")
            false
        }
    }

    //kilp wallet address의 정보제공을 위한 함수
    fun postWalletAuth(body: com.dragonguard.android.data.model.klip.WalletAuthRequestModel): com.dragonguard.android.data.model.klip.WalletAuthResponseModel {
        var authResult =
            com.dragonguard.android.data.model.klip.WalletAuthResponseModel(null, null, null)
        val retrofitWallet = Retrofit.Builder().baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
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
    fun getAuthResult(key: String): com.dragonguard.android.data.model.klip.WalletAuthResultModel {
        var authResult =
            com.dragonguard.android.data.model.klip.WalletAuthResultModel(null, null, null, null)
        val retrofitWallet = Retrofit.Builder().baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
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

    //두 Repository의 구성원들의 기여도를 받아오기 위한 함수
    fun postCompareRepoMembersRequest(
        body: com.dragonguard.android.data.model.compare.CompareRepoRequestModel,
        token: String
    ): com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult =
            com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel(null, null)
        val compareRepoMembers = api.postCompareRepoMembers(body, "Bearer $token")
        try {
            val result = compareRepoMembers.execute()
            Log.d("token", "사용자 비교 결과 ${result.code()}")
            compareRepoResult = result.body()!!

        } catch (e: Exception) {
            Log.d("token", "사용자 비교 실패 ${e.message}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    //두 Repository의 정보를 받아오기 위한 함수
    fun postCompareRepoRequest(
        body: com.dragonguard.android.data.model.compare.CompareRepoRequestModel,
        token: String
    ): com.dragonguard.android.data.model.compare.CompareRepoResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult =
            com.dragonguard.android.data.model.compare.CompareRepoResponseModel(null, null)
        val compareRepo = api.postCompareRepo(body, "Bearer $token")
        try {
            val result = compareRepo.execute()
            Log.d("token", "레포 비교 결과 ${result.code()}")
            compareRepoResult = result.body()!!
        } catch (e: Exception) {
            Log.d("token", "레포 비교 실패 ${e.message}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    fun getNewAccessToken(
        access: String,
        refresh: String
    ): com.dragonguard.android.data.model.token.RefreshTokenModel {
        var newToken = com.dragonguard.android.data.model.token.RefreshTokenModel(null, null, null)
        val getToken = api.getNewAccessToken(access, refresh)
        try {
            val result = getToken.execute()
            newToken = result.body()!!
            Log.d("e", "access token 받아오기 결과 ${result.code()} ")
        } catch (e: Exception) {
            Log.d("e", "access token 받아오기 실패 ${e.message}")
            return newToken
        }
        return newToken
    }

    fun postCommits(token: String) {
        val postCommit = api.postCommits("Bearer $token")
        try {
            val result = postCommit.execute()
            Log.d("postCommits", "commit 업데이트 결과 ${result.code()}")
        } catch (e: Exception) {
            Log.d("e", "commit 업데이트 실패 ${e.message}")
        }
    }

    fun getOrgNames(
        name: String,
        token: String,
        count: Int,
        type: String
    ): com.dragonguard.android.data.model.org.OrganizationNamesModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", "$count")
        queryMap.put("name", name)
        queryMap.put("type", type)
        queryMap.put("size", "10")
        val getOrgNames = api.getOrgNames(queryMap, "Bearer $token")
        var orgNames = com.dragonguard.android.data.model.org.OrganizationNamesModel()
        orgNames.add(
            com.dragonguard.android.data.model.org.OrganizationNamesModelItem(
                null,
                null,
                null,
                null,
                null
            )
        )
        return try {
            val result = getOrgNames.execute()
            Log.d("error", "조직 이름 찾기 결과 : ${result.code()}")
            orgNames = result.body()!!
            orgNames
        } catch (e: Exception) {
            Log.d("error", "조직 이름 찾기 error : ${e.message}")
            orgNames
        }
    }

    fun postRegistOrg(
        body: com.dragonguard.android.data.model.org.RegistOrgModel,
        token: String
    ): com.dragonguard.android.data.model.org.RegistOrgResultModel {
        val postRegist = api.postOrgRegist(body, "Bearer $token")
        var registResult = com.dragonguard.android.data.model.org.RegistOrgResultModel(0)
        return try {
            val result = postRegist.execute()
            Log.d("error", "RegisterOrganization error: ${result.code()}}")
            registResult = result.body()!!
            registResult
        } catch (e: Exception) {
            Log.d("error", "RegisterOrganization error: ${e.message}")
            registResult
        }
    }

    fun addOrgMember(
        body: com.dragonguard.android.data.model.org.AddOrgMemberModel,
        token: String
    ): Long {
        val addOrg = api.postAddOrgMember(body, "Bearer $token")
        return try {
            val result = addOrg.execute()
            Log.d("status", "조직 멤버 추가 결과 : ${result.code()}")
            result.body()!!.id
        } catch (e: Exception) {
            Log.d("status", "조직 멤버 추가 error : ${e.message}")
            -1
        }
    }

    fun sendEmailAuth(token: String): Long {
        val sendEmail = api.postAuthEmail("Bearer $token")
        return try {
            val result = sendEmail.execute()
            Log.d("status", "이메일 인증 시도 결과 : ${result.code()}")
            result.body()!!.id
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 시도 error : ${e.message}")
            -1L
        }
    }

    fun emailAuthResult(id: Long, code: String, orgId: Long, token: String): Boolean {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("id", id.toString())
        queryMap.put("code", code)
        queryMap.put("organizationId", orgId.toString())
        val emailAuth = api.getEmailAuthResult(queryMap, "Bearer $token")
        return try {
            val result = emailAuth.execute()
            Log.d("status", "이메일 인증 결과 : ${result.code()}")
            result.body()!!.is_valid_code
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 결과 error : ${e.message}")
            false
        }
    }

    fun deleteEmailCode(id: Long, token: String): Boolean {
        val delete = api.deleteEmailCode(id, "Bearer $token")
        return try {
            val result = delete.execute()
            Log.d("status", "이메일 인증 코드 삭제 성공: ${result.code()}")
            true
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 코드 삭제 실패: ${e.message}")
            false
        }
    }

    fun searchOrgId(orgName: String, token: String): Long {
        val searchId = api.getOrgId(orgName, "Bearer $token")
        val resultId = 0L
        return try {
            val result = searchId.execute()
            Log.d("status", "조직 id 검색 결과: ${result.code()}")
            return result.body()!!.id
        } catch (e: Exception) {
            Log.d("status", "조직 id 검색 실패: ${e.message}")
            resultId
        }
    }

    fun orgInternalRankings(
        id: Long,
        count: Int,
        token: String
    ): com.dragonguard.android.data.model.rankings.OrgInternalRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("organizationId", id.toString())
        queryMap.put("page", count.toString())
        queryMap.put("size", "20")

        val orgRankings = com.dragonguard.android.data.model.rankings.OrgInternalRankingModel()
        val orgInternal = api.getOrgInternalRankings(queryMap, "Bearer $token")

        return try {
            val result = orgInternal.execute()
            Log.d("error", "조직 내 사용자들의 랭킹 조회 결과: ${result.code()} ")
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "조직 내 사용자들의 랭킹 조회 실패: ${e.message} ")
            return orgRankings
        }
    }

    fun typeOrgRanking(
        type: String,
        page: Int,
        token: String
    ): com.dragonguard.android.data.model.rankings.OrganizationRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("type", type)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")

        val orgRankings = com.dragonguard.android.data.model.rankings.OrganizationRankingModel()
        val orgTotal = api.getOrgRankings(queryMap, "Bearer $token")
        return try {
            val result = orgTotal.execute()
            Log.d("error", "$type 조회 결과: ${result.code()} ")
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "$type 조회 실패: ${e.message} ")
            return orgRankings
        }
    }

    fun allOrgRanking(
        page: Int,
        token: String
    ): com.dragonguard.android.data.model.rankings.OrganizationRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        val orgRankings = com.dragonguard.android.data.model.rankings.OrganizationRankingModel()
        val orgTotal = api.getAllOrgRankings(queryMap, "Bearer $token")
        return try {
            val result = orgTotal.execute()
            Log.d("error", "전체 조직 랭킹 조회 결과: ${result.code()} ")
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "전체 조직 랭킹 조회 실패: ${e.message} ")
            return orgRankings
        }
    }


    fun checkAdmin(token: String): Boolean {
        val check = api.getPermissionState("Bearer $token")
        try {
            val result = check.execute()
            if (result.isSuccessful) {
                return true
            } else if (result.code() == 403) {
                Log.d("error", "admin error result ${result.code()}")
                return false
            }
        } catch (e: HttpException) {
            if (e.code() == 403) {
                Log.d("error", "admin error ${e.code()}")
                return false
            }
        } catch (e: Exception) {
            Log.d("error", "admin error")
        }
        return false
    }

    fun approveOrgRequest(
        body: com.dragonguard.android.data.model.org.OrgApprovalModel,
        token: String
    ): com.dragonguard.android.data.model.org.ApproveRequestOrgModel {
        val approve = api.postOrgApproval(body, "Bearer $token")
        try {
            val result = approve.execute()
            Log.d("result", "admin ${result.code()}")
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "admin error ${e.message}")
        }
        return com.dragonguard.android.data.model.org.ApproveRequestOrgModel()
    }

    fun statusOrgList(
        status: String,
        page: Int,
        token: String
    ): com.dragonguard.android.data.model.org.ApproveRequestOrgModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("status", status)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")

        val statusOrg = com.dragonguard.android.data.model.org.ApproveRequestOrgModel()
        val orgList = api.getOrgStatus(queryMap, "Bearer $token")
        return try {
            val result = orgList.execute()
            Log.d("result", "승인된 조직 조회 결과: ${result.code()} ")
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "승인된 조직 조회 실패: ${e.message} ")
            return statusOrg
        }
    }

    fun userGitOrgRepoList(
        orgName: String,
        token: String
    ): com.dragonguard.android.data.model.GithubOrgReposModel? {
        val repoList = api.getOrgRepoList(orgName, "Bearer $token")
        return try {
            val result = repoList.execute()
            Log.d("result", "git org 레포 리스트 조회 결과: ${result.code()}")
            result.body()
        } catch (e: Exception) {
            Log.d("error", "git org 레포 리스트 조회 실패: ${e.message}")
            null
        }
    }

    fun otherProfile(
        githubId: String,
        token: String
    ): com.dragonguard.android.data.model.detail.UserProfileModel? {
        val profile = api.getOthersProfile(githubId, "Bearer $token")
        return try {
            val result = profile.execute()
            Log.d("result", "타인의 프로필 호출 결과 ${result.code()}")
            Log.d("result", "타인의 프로필 호출 결과 ${result.message()}")
            result.body()
        } catch (e: Exception) {
            Log.d("error", "타인의 프로필 조회 실패: ${e.message}")
            null
        }
    }

    fun manualCompareMembers(
        body: com.dragonguard.android.data.model.compare.CompareRepoRequestModel,
        token: String
    ): com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult =
            com.dragonguard.android.data.model.compare.CompareRepoMembersResponseModel(null, null)
        val compareRepoMembers = api.postCompareRepoMembersUpdate(body, "Bearer $token")
        try {
            val result = compareRepoMembers.execute()
            Log.d("result", "레포 비교 업데이트 멤버 결과 ${result.code()}")
            compareRepoResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 비교 업데이트 멤버 에러 ${e.message}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    fun manualCompareRepo(
        body: com.dragonguard.android.data.model.compare.CompareRepoRequestModel,
        token: String
    ): com.dragonguard.android.data.model.compare.CompareRepoResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult =
            com.dragonguard.android.data.model.compare.CompareRepoResponseModel(null, null)
        val compareRepo = api.postCompareRepoUpdate(body, "Bearer $token")
        try {
            val result = compareRepo.execute()
            Log.d("result", "레포 비교 업데이트 결과 ${result.code()}")
            compareRepoResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 비교 업데이트 에러 ${e.message}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    fun manualContribute(
        repoName: String,
        token: String
    ): com.dragonguard.android.data.model.contributors.RepoContributorsModel {
        val repoContributors = api.getRepoContributorsUpdate(repoName, "Bearer $token")
        var repoContResult =
            com.dragonguard.android.data.model.contributors.RepoContributorsModel(null, null)
        try {
            val result = repoContributors.execute()
            Log.d("result", "레포 상세조회 업데이트 결과 ${result.code()}")
            repoContResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "레포 상세조회 업데이트 에러 ${e.message}")
            return repoContResult
        }
        return repoContResult
    }

    fun manualToken(token: String): ArrayList<com.dragonguard.android.data.model.klip.TokenHistoryModelItem>? {
        val tokenHistory = api.updateToken("Bearer $token")
        var tokenHistoryResult: ArrayList<com.dragonguard.android.data.model.klip.TokenHistoryModelItem>? =
            null
        try {
            val result = tokenHistory.execute()
            Log.d("result", "블록체인 부여내역 업데이트 결과 ${result.code()}")
            tokenHistoryResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "블록체인 부여내역 업데이트 오류 ${e.message}")
            return null
        }
        return tokenHistoryResult
    }

    fun manualUserInfo(token: String): com.dragonguard.android.data.model.UserInfoModel {
        val userInfo = api.userInfoUpdate("Bearer $token")
        var userResult = com.dragonguard.android.data.model.UserInfoModel(
            null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null
        )
        try {
            val result = userInfo.execute()
            Log.d("result", "사용자 정보 업데이트 결과 : ${result.code()}")
            userResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "사용자 정보 업데이트 에러 : ${e.message}")
            return userResult
        }
        return userResult
    }

    fun getLoginState(token: String): Boolean? {
        val authState = api.getLoginAuthState("Bearer $token")

        return try {
            val result = authState.execute()
            Log.d("result", "로그인 상태 확인 결과: ${result.code()}")
            if (result.code() == 200) {
                return result.body()!!.is_login_user
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.d("error", "로그인 상태 확인 오류: ${e.message}")
            false
        }
    }

    fun withDrawAccount(token: String): Boolean {
        val withDraw = api.postWithDraw("Bearer $token")
        Log.d("token", "token: $token")
        return try {
            val result = withDraw.execute()
            Log.d("token", "code: ${result.code()}")
            result.code() == 204
        } catch (e: Exception) {
            Log.d("error", "회원 탈퇴 실패 오류: ${e.message}")
            false
        }

    }
}