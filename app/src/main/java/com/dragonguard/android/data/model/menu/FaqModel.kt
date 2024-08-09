package com.dragonguard.android.data.model.menu

data class FaqModel(
    val title: String,
    val content: String,
    var expandable: Boolean = false
)
