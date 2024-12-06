package com.dragonguard.android.ui.ranking.outer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dragonguard.android.R
import com.dragonguard.android.databinding.FragmentRankingBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment(private val userName: String) : Fragment() {

    private lateinit var binding: FragmentRankingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentContent.adapter = FragmentAdapter(this, userName)
        val tabs = arrayOf("사용자 전체", "조직 전체", "회사", "대학교", "고등학교", "ETC")
        TabLayoutMediator(binding.rankingTab, binding.fragmentContent) { tab, position ->
            val tabView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_text, null) as TextView
            tabView.text = tabs[position]
            tab.customView = tabView
//            tab.text = tabs[position]
        }.attach()
    }

}