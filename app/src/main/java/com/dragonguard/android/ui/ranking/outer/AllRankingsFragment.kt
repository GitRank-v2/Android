package com.dragonguard.android.ui.ranking.outer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.databinding.FragmentAllRankingsBinding
import com.dragonguard.android.ui.profile.other.OthersProfileActivity
import com.dragonguard.android.ui.ranking.OrganizationInternalActivity
import com.dragonguard.android.ui.ranking.RankingsAdapter
import com.dragonguard.android.util.CustomGlide
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllRankingsFragment(private val rankingType: String, private val userName: String) :
    Fragment(), RankingsAdapter.OnRankingClickListener {
    private lateinit var binding: FragmentAllRankingsBinding
    private val viewModel by viewModels<RankingsViewModel>()
    private val size = 20
    private var page = 0
    private var position = 0
    private var ranking = 0
    private var changed = true
    private var current = 0
    private lateinit var rankingsAdapter: RankingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllRankingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("type", "랭킹 type: $rankingType")
        initObserver()
        binding.rankingLottie.visibility = View.VISIBLE
        binding.rankingLottie.playAnimation()
        when (rankingType) {
            "사용자 전체" -> {
                viewModel.setTypeName("사용자 전체")
                viewModel.setTypeToUser()
                viewModel.getTotalUserRanking(page, size)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking,
                    this
                )
            }

            "조직 전체" -> {
                viewModel.setTypeName("조직 전체")
                viewModel.getTotalOrganizationRanking(page)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                    this
                )
            }

            "회사" -> {
                viewModel.setTypeName("회사")
                viewModel.getCompanyRanking(page)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                    this
                )
            }

            "대학교" -> {
                viewModel.setTypeName("대학교")
                viewModel.getUniversityRanking(page)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                    this
                )
            }

            "고등학교" -> {
                viewModel.setTypeName("고등학교")
                viewModel.getHighSchoolRanking(page)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                    this
                )
            }

            "ETC" -> {
                viewModel.setTypeName("ETC")
                viewModel.getEtcRanking(page)
                RankingsAdapter(
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                    this
                )
            }
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.loadState == LoadState.SUCCESS) {
                        when (it.type.type) {
                            "사용자 전체" -> {
                                Log.d("랭킹", "사용자 전체 랭킹")
                                if (page == current) {
                                    checkTotalUserRankings()
                                }
                            }

                            else -> {
                                if (page == current) {
                                    checkOrgRankings()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun checkTotalUserRankings() {
        if (viewModel.currentState.ranking.ranking.isNotEmpty()) {
            (viewModel.currentState.ranking as RankingsContract.RankingsState.Rankings.AllUsers.Ranking).userRanking.forEach {
                if (ranking != 0) {
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking.add(
                        TotalUsersRankingsModel(
                            it.contribution_amount,
                            it.github_id,
                            it.id,
                            it.tier,
                            ranking + 1L,
                            it.profile_image
                        )
                    )
                } else {
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking.add(
                        TotalUsersRankingsModel(
                            it.contribution_amount,
                            it.github_id,
                            it.id,
                            it.tier,
                            1,
                            it.profile_image
                        )
                    )
                }
                //Log.d("유져", "랭킹 ${ranking+1} 추가")
                ranking++
            }

            viewModel.addUserRanking()
            initUserRecycler()
        } else {
            binding.rankingLottie.pauseAnimation()
            binding.rankingLottie.visibility = View.GONE
        }
    }

    private fun checkOrgRankings() {
        if (viewModel.currentState.ranking.ranking.isNotEmpty()) {
            (viewModel.currentState.ranking as RankingsContract.RankingsState.Rankings.Organization.Ranking).orgRanking.forEach {
                if (ranking != 0) {
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).rankings.add(
                        TotalOrganizationModel(
                            id = it.id,
                            name = it.name,
                            contribution_amount = it.contribution_amount,
                            ranking = ranking + 1,
                            type = it.type
                        )
                    )
                } else {
                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).rankings.add(
                        TotalOrganizationModel(
                            id = it.id,
                            name = it.name,
                            contribution_amount = it.contribution_amount,
                            ranking = 1,
                            type = it.type
                        )
                    )
                }
                //Log.d("유져", "랭킹 ${ranking+1} 추가")
                ranking++
            }
            viewModel.addOrganizationRanking()
            initOrgRecycler()
        } else {
            binding.rankingLottie.pauseAnimation()
            binding.rankingLottie.visibility = View.GONE
        }
    }

    private fun initUserRecycler() {
        binding.eachRankings.setItemViewCacheSize(viewModel.currentState.rankings.ranking.size)
        binding.eachRankings.setHasFixedSize(true)
        binding.eachRankings.isNestedScrollingEnabled = true
        //Toast.makeText(applicationContext, "개수 : ${usersRanking.size}",Toast.LENGTH_SHORT).show()
        if (page == 0) {
            when (viewModel.currentState.rankings.ranking.size) {
                1 -> {
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[0],
                        1
                    )
                }

                2 -> {
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[0],
                        1
                    )
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[1],
                        2
                    )
                }

                3 -> {
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[0],
                        1
                    )
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[1],
                        2
                    )
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[2],
                        3
                    )
                }

                else -> {
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[0],
                        1
                    )
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[1],
                        2
                    )
                    profileBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking[2],
                        3
                    )

                    viewModel.currentState.rankings.ranking.removeAt(0)
                    viewModel.currentState.rankings.ranking.removeAt(0)
                    viewModel.currentState.rankings.ranking.removeAt(0)
                    if (this@AllRankingsFragment.isAdded && !this@AllRankingsFragment.isDetached && this@AllRankingsFragment.isVisible && !this@AllRankingsFragment.isRemoving) {
                        rankingsAdapter = RankingsAdapter(
                            (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.AllUsers.Rankings).userRanking,
                            this
                        )
                        binding.eachRankings.adapter = rankingsAdapter
                        val layoutmanager = LinearLayoutManager(requireContext())
                        layoutmanager.initialPrefetchItemCount = 4
                        binding.eachRankings.layoutManager = layoutmanager
                        //rankingsAdapter.notifyDataSetChanged()
                        binding.eachRankings.visibility = View.VISIBLE
                    }

                }
            }
        }
        binding.eachRankings.adapter?.notifyDataSetChanged()
        page++
        if ((page * size) - 3 == viewModel.currentState.rankings.ranking.size) {
            current++
        }
        Log.d("api 횟수", "$page 페이지 검색")
        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        Log.d("전부", "유저 랭킹: ${viewModel.currentState.rankings.ranking}")
        initScrollListener()
    }

    private fun initOrgRecycler() {
        if (page == 0) {
            when (viewModel.currentState.rankings.ranking.size) {
                1 -> {
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[0],
                        1
                    )
                }

                2 -> {
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[0],
                        1
                    )
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[1],
                        2
                    )
                }

                3 -> {
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[0],
                        1
                    )
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[1],
                        2
                    )
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[2],
                        3
                    )
                }

                else -> {
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[0],
                        1
                    )
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[1],
                        2
                    )
                    profileOrgBackground(
                        (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking[2],
                        3
                    )

                    viewModel.currentState.rankings.ranking.removeAt(0)
                    viewModel.currentState.rankings.ranking.removeAt(0)
                    viewModel.currentState.rankings.ranking.removeAt(0)

                    if (this@AllRankingsFragment.isAdded && !this@AllRankingsFragment.isDetached && this@AllRankingsFragment.isVisible && !this@AllRankingsFragment.isRemoving) {
                        when (rankingType) {
                            "회사" -> {
                                rankingsAdapter = RankingsAdapter(
                                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                                    this
                                )
                            }

                            "대학교" -> {
                                rankingsAdapter = RankingsAdapter(
                                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                                    this
                                )
                            }

                            "고등학교" -> {
                                rankingsAdapter = RankingsAdapter(
                                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                                    this
                                )
                            }

                            "ETC" -> {
                                rankingsAdapter = RankingsAdapter(
                                    (viewModel.currentState.rankings as RankingsContract.RankingsState.Rankings.Organization.Rankings).orgRanking,
                                    this
                                )
                            }
                        }
                        binding.eachRankings.adapter = rankingsAdapter
                        val layoutmanager = LinearLayoutManager(requireContext())
                        layoutmanager.initialPrefetchItemCount = 10
                        binding.eachRankings.layoutManager = layoutmanager
                        binding.eachRankings.layoutManager = LinearLayoutManager(requireContext())
                        binding.eachRankings.visibility = View.VISIBLE
                    }

                }
            }
        }
        binding.eachRankings.adapter?.notifyDataSetChanged()
        page++
        if ((page * size) - 3 == viewModel.currentState.rankings.ranking.size) {
            current++
        }
        Log.d("api 횟수", "$page 페이지 검색")
        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        binding.topRankings.visibility = View.VISIBLE
        initScrollListener()
    }

    private fun profileBackground(model: TotalUsersRankingsModel, number: Int) {
        binding.firstProfile.clipToOutline = true
        binding.secondProfile.clipToOutline = true
        binding.thirdProfile.clipToOutline = true
        when (number) {
            1 -> {
                when (model.tier) {
                    "BRONZE" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }

                    "SILVER" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }

                    "GOLD" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }

                    "PLATINUM" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }

                    "DIAMOND" -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }

                    else -> {
                        binding.firstFrame.setBackgroundResource(R.drawable.shadow_unrank)
                    }
                }
                binding.firstId.text = model.github_id
                CustomGlide.drawImage(binding.firstProfile, model.profile_image) { }

                binding.firstContribute.text = model.contribution_amount.toString()
                binding.firstFrame.setOnClickListener {
                    val intent = Intent(requireContext(), OthersProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    startActivity(intent)
                }
                binding.firstRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }

            2 -> {
                when (model.tier) {
                    "BRONZE" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }

                    "SILVER" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }

                    "GOLD" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }

                    "PLATINUM" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }

                    "DIAMOND" -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }

                    else -> {
                        binding.secondFrame.setBackgroundResource(R.drawable.shadow_unrank)
                    }
                }
                binding.secondId.text = model.github_id
                CustomGlide.drawImage(binding.secondProfile, model.profile_image) { }
                binding.secondContribute.text = model.contribution_amount.toString()
                binding.secondFrame.setOnClickListener {
                    val intent = Intent(requireContext(), OthersProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    startActivity(intent)
                }
                binding.secondRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }

            3 -> {
                when (model.tier) {
                    "BRONZE" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_bronze)

                    }

                    "SILVER" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_silver)
                    }

                    "GOLD" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_gold)
                    }

                    "PLATINUM" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_platinum)
                    }

                    "DIAMOND" -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_diamond)
                    }

                    else -> {
                        binding.thirdFrame.setBackgroundResource(R.drawable.shadow_unrank)
                    }
                }
                binding.thirdId.text = model.github_id
                CustomGlide.drawImage(binding.thirdProfile, model.profile_image) { }
                binding.thirdContribute.text = model.contribution_amount.toString()
                binding.thirdFrame.setOnClickListener {
                    val intent = Intent(requireContext(), OthersProfileActivity::class.java)
                    intent.putExtra("userName", model.github_id)
                    startActivity(intent)
                }
                binding.thirdRanker.visibility = View.VISIBLE
                binding.topRankings.visibility = View.VISIBLE
            }
        }
    }

    private fun profileOrgBackground(model: TotalOrganizationModel, number: Int) {
        when (number) {
            1 -> {
                //Glide.with(binding.firstProfile).load()
                //.into(binding.firstProfile)
                binding.firstId.text = model.name
                binding.firstContribute.text = model.contribution_amount.toString()
                binding.firstRanker.visibility = View.VISIBLE
                binding.firstProfile.setImageResource(R.drawable.company)
                binding.firstFrame.setOnClickListener {
                    val intent = Intent(context, OrganizationInternalActivity::class.java)
                    intent.putExtra("organization", model.name)
                    startActivity(intent)
                }
            }

            2 -> {
                binding.secondId.text = model.name
                binding.secondContribute.text = model.contribution_amount.toString()
                binding.secondRanker.visibility = View.VISIBLE
                binding.secondProfile.setImageResource(R.drawable.company)
                binding.secondFrame.setOnClickListener {
                    val intent = Intent(context, OrganizationInternalActivity::class.java)
                    intent.putExtra("organization", model.name)
                    startActivity(intent)
                }
            }

            3 -> {
                binding.thirdId.text = model.name
                binding.thirdContribute.text = model.contribution_amount.toString()
                binding.thirdRanker.visibility = View.VISIBLE
                binding.thirdProfile.setImageResource(R.drawable.company)
                binding.thirdFrame.setOnClickListener {
                    val intent = Intent(context, OrganizationInternalActivity::class.java)
                    intent.putExtra("organization", model.name)
                    startActivity(intent)
                }
            }
        }
    }


    private fun initScrollListener() {
        binding.eachRankings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.eachRankings.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                Log.d("마지막 item", lastVisibleItem.toString())
                Log.d("갯수", itemTotalCount.toString())
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.eachRankings.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    when (rankingType) {
                        "사용자 전체" -> {
                            if (viewModel.currentState.rankings.ranking.size >= (page + 1) * size) {
                                loadMoreUserRanking()
                            }
                        }

                        "조직 전체" -> {
                            loadMoreTotalOrgRanking()
                        }

                        else -> {
                            loadMoreTypeOrgRanking()
                        }
                    }
                }
            }
        })
    }

    private fun loadMoreUserRanking() {
        Log.d("랭킹", "유저 랭킹 추가")
        if (binding.rankingLottie.visibility == View.GONE) {
            binding.rankingLottie.visibility = View.VISIBLE
            binding.rankingLottie.playAnimation()
            changed = true
            viewModel.getTotalUserRanking(page, size)
        }
    }

    private fun loadMoreTotalOrgRanking() {
        if (binding.rankingLottie.visibility == View.GONE) {
            binding.rankingLottie.visibility = View.VISIBLE
            binding.rankingLottie.playAnimation()
            changed = true
            viewModel.getTotalOrganizationRanking(page)
        }
    }

    private fun loadMoreTypeOrgRanking() {
        if (binding.rankingLottie.visibility == View.GONE) {
            binding.rankingLottie.visibility = View.VISIBLE
            binding.rankingLottie.playAnimation()
            changed = true
            when (rankingType) {
                "회사" -> {
                    viewModel.getCompanyRanking(page)
                }

                "대학교" -> {
                    viewModel.getUniversityRanking(page)
                }

                "고등학교" -> {
                    viewModel.getHighSchoolRanking(page)
                }

                "ETC" -> {
                    viewModel.getEtcRanking(page)
                }
            }
        }
    }

    override fun onUserRankingClick(userName: String) {
        val intent = Intent(context, OthersProfileActivity::class.java)
        intent.putExtra("userName", userName)
        if (userName == this.userName) {
            intent.putExtra("isUser", true)
        }
        startActivity(intent)
    }

    override fun onOrgInternalRankingClick(orgName: String) = Unit

    override fun onOrgInternalRankingUserClick(userName: String) = Unit

    override fun onOrgRankingClick(orgName: String) {
        val intent = Intent(context, OrganizationInternalActivity::class.java)
        intent.putExtra("organization", orgName)
        startActivity(intent)
    }
}