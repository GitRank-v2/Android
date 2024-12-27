package com.dragonguard.android.ui.ranking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModelItem
import com.dragonguard.android.data.model.rankings.OrgInternalRankingsModel
import com.dragonguard.android.databinding.ActivityOrganizationInternalRankingBinding
import com.dragonguard.android.ui.profile.other.OthersProfileActivity
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
 사용자의 대학교 내의 랭킹을 보여주는 activity
 */
@AndroidEntryPoint
class OrganizationInternalActivity : AppCompatActivity(), RankingsAdapter.OnRankingClickListener {
    private lateinit var binding: ActivityOrganizationInternalRankingBinding
    private val viewModel by viewModels<OrganizationInternalViewModel>()
    private var orgName = ""
    private var page = 0
    private var position = 0
    private var changed = true
    private var ranking = 0
    private var orgInternalRankings =
        ArrayList<OrgInternalRankingsModel>()
    private lateinit var organizationInternalRankingAdapter: RankingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_organization_internal_ranking)
        initObserver()

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        orgName = intent.getStringExtra("organization")!!
        Log.d("name", orgName)
        searchOrgId()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        orgInternalRankings(state.orgId.orgId)
                    }

                    if (state.loadState == LoadState.REFRESH) {
                        checkRankings(state.receivedRankings.orgInternalRankings)
                    }
                }
            }
        }
    }

    private fun searchOrgId() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.searchOrgId(orgName)
    }

    private fun orgInternalRankings(id: Long) {
        binding.orgInternalName.text = orgName
        viewModel.getOrgInternalRankings(id, page)
    }

    private fun checkRankings(result: List<OrgInternalRankingModelItem>) {
        if (result.isNotEmpty()) {
            Log.d("조직내부랭킹", "결과 : $result")
            result.forEach {
                Log.d("조직내부랭킹", "결과 : ${it.github_id}")
                if (ranking != 0) {
                    if (orgInternalRankings[ranking - 1].tokens == it.contribution_amount) {
                        orgInternalRankings.add(
                            OrgInternalRankingsModel(
                                it.github_id, it.id, it.tier, it.contribution_amount,
                                orgInternalRankings[ranking - 1].ranking,
                                it.profile_image
                            )
                        )
                    } else {
                        orgInternalRankings.add(
                            OrgInternalRankingsModel(
                                it.github_id,
                                it.id,
                                it.tier,
                                it.contribution_amount,
                                ranking + 1,
                                it.profile_image
                            )
                        )
                    }
                } else {
                    orgInternalRankings.add(
                        OrgInternalRankingsModel(
                            it.github_id,
                            it.id,
                            it.tier,
                            it.contribution_amount,
                            1,
                            it.profile_image
                        )
                    )
                }
//                Log.d("유져", "랭킹 ${ranking+1} 추가")
                ranking++
            }
            Log.d("뷰 보이기 전", "initrecycler 전")

            initRecycler()

        }
    }

    private fun initRecycler() {
        Log.d("recycler", "initrecycler()")
        binding.orgInternalRanking.setItemViewCacheSize(orgInternalRankings.size)
        if (page == 0) {
            organizationInternalRankingAdapter =
                RankingsAdapter(orgInternalRankings, this)
            binding.orgInternalRanking.adapter = organizationInternalRankingAdapter
            binding.orgInternalRanking.layoutManager = LinearLayoutManager(this)
            binding.orgInternalRanking.visibility = View.VISIBLE
        }
        organizationInternalRankingAdapter.notifyDataSetChanged()
        page++
        Log.d("api 횟수", "$page 페이지 검색")
        binding.progressBar.visibility = View.GONE
        initScrollListener()
        viewModel.resetState()
    }

    private fun loadMorePosts() {
        if (binding.progressBar.visibility == View.GONE && orgInternalRankings.size >= 10 * page) {
            binding.progressBar.visibility = View.VISIBLE
            changed = true
            orgInternalRankings(viewModel.currentState.orgId.orgId)
        }
    }

    private fun initScrollListener() {
        binding.orgInternalRanking.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.orgInternalRanking.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.orgInternalRanking.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadMorePosts()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUserRankingClick(userName: String) = Unit

    override fun onOrgInternalRankingClick(orgName: String) {
        val intent = Intent(this, OrganizationInternalActivity::class.java)
        intent.putExtra("organization", orgName)
        startActivity(intent)
    }

    override fun onOrgInternalRankingUserClick(userName: String) {
        val intent = Intent(this, OthersProfileActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }

    override fun onOrgRankingClick(orgName: String) = Unit
}
