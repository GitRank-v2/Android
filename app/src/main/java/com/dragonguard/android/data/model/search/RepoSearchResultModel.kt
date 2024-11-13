package com.dragonguard.android.data.model.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/*
 repo 이름을 받기위해 정의한 model
 */
@JsonClass(generateAdapter = true)
data class RepoSearchResultModel(
    @field:Json(name = "id")
    val id: Long,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "language")
    val language: String?,
    @field:Json(name = "description")
    val description: String?,
    @field:Json(name = "created_at")
    val created_at: String?
) : Serializable