package com.dragonguard.android.data.model.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 검색어로 검색한 repo의 이름을 받기위해 정의한 model
 RepoSearchResultModel 의 리스트이다
 */
@JsonClass(generateAdapter = true)
data class RepoNameModel(
    @field:Json(name = "data")
    val data: List<RepoSearchResultModel>
)