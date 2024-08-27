package com.dragonguard.android.ui.ranking

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragment: Fragment, private val token: String) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = fragmentList.size

    private val fragmentList = listOf<String>("사용자 전체", "조직 전체", "회사", "대학교", "고등학교", "ETC")

    override fun createFragment(position: Int): Fragment {
        val fragmentTag = fragmentList[position]
        return AllRankingsFragment(fragmentTag)
    }
}