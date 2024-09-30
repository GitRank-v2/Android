package com.dragonguard.android.ui.ranking

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.data.model.rankings.OrgInternalRankingsModel
import com.dragonguard.android.databinding.ActivityOrganizationInternalRankingBinding
import com.dragonguard.android.util.LoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
 사용자의 대학교 내의 랭킹을 보여주는 activity
 */
class OrganizationInternalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrganizationInternalRankingBinding
    private lateinit var viewModel: OrganizationInternalViewModel
    private var orgName = ""
    private var page = 0
    private var position = 0
    private var id = 0L
    private var changed = true
    private var ranking = 0
    private var orgInternalRankings =
        ArrayList<OrgInternalRankingsModel>()
    private lateinit var organizationInternalRankingAdapter: RankingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_organization_internal_ranking)
        viewModel = OrganizationInternalViewModel()
        initObserver()

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        orgName = intent.getStringExtra("organization")!!
        searchOrgId()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.orgId.orgId != -1L) {
                        orgInternalRankings(state.orgId.orgId)
                    }

                    if (state.loadState == LoadState.SUCCESS) {
                        checkRankings(state.orgInternalRankings.orgInternalRankings)
                    }
                }
            }
        }
    }

    private fun searchOrgId() {
        binding.progressBar.visibility = View.VISIBLE
        //viewModel.searchOrgId(orgName)
    }

    private fun orgInternalRankings(id: Long) {
        binding.orgInternalName.text = orgName
        //viewModel.getOrgInternalRankings(id, page)
    }

    private fun checkRankings(result: OrgInternalRankingModel) {
        if (result.isNotEmpty()) {
            Log.d("조직 내 랭킹", "결과 : ${result[0].github_id}")
            result.forEach {
                Log.d("조직 내 랭킹", "결과 : ${it.github_id}")
                if (ranking != 0) {
                    if (orgInternalRankings[ranking - 1].tokens == it.tokens) {
                        orgInternalRankings.add(
                            OrgInternalRankingsModel(
                                it.github_id, it.id, it.name, it.tier, it.tokens,
                                orgInternalRankings[ranking - 1].ranking,
                                it.profile_image
                            )
                        )
                    } else {
                        orgInternalRankings.add(
                            OrgInternalRankingsModel(
                                it.github_id,
                                it.id,
                                it.name,
                                it.tier,
                                it.tokens,
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
                            it.name,
                            it.tier,
                            it.tokens,
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

        } else {
            if (changed) {
                changed = false
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ orgInternalRankings(id) }, 4000)
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun initRecycler() {
        Log.d("recycler", "initrecycler()")
        binding.orgInternalRanking.setItemViewCacheSize(orgInternalRankings.size)
        if (page == 0) {
            organizationInternalRankingAdapter =
                RankingsAdapter(orgInternalRankings, this, viewModel.currentState.token.token)
            binding.orgInternalRanking.adapter = organizationInternalRankingAdapter
            binding.orgInternalRanking.layoutManager = LinearLayoutManager(this)
            binding.orgInternalRanking.visibility = View.VISIBLE
        }
        binding.orgInternalRanking.adapter?.notifyDataSetChanged()
        page++
        Log.d("api 횟수", "$page 페이지 검색")
        binding.progressBar.visibility = View.GONE
        initScrollListener()
    }

    private fun loadMorePosts() {
        if (binding.progressBar.visibility == View.GONE) {
            binding.progressBar.visibility = View.VISIBLE
            changed = true
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("api 시도", "getTotalUsersRanking 실행  load more")
                orgInternalRankings(id)
            }
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
}
