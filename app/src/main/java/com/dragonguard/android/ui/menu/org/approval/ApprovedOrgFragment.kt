package com.dragonguard.android.ui.menu.org.approval

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
import com.dragonguard.android.databinding.FragmentApprovedOrgBinding
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApprovedOrgFragment : Fragment() {
    private lateinit var binding: FragmentApprovedOrgBinding
    private val viewModel by viewModels<ApprovedOrgViewModel>()
    private val adapter by lazy { ApprovedOrgAdapter() }
    private var count = 0
    private var page = 0
    private var position = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApprovedOrgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.state == LoadState.SUCCESS) {
                        viewModel.addReceivedOrg()
                    }

                    if (state.state == LoadState.REFRESH) {
                        initRecycler()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getApprovedOrg(page)
    }


    private fun initRecycler() {
        Log.d("count", "count: $count")
        if (page == 0) {

            binding.acceptedOrgList.adapter = adapter
            binding.acceptedOrgList.layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("list", "결과 : ${viewModel.currentState.approvedOrg.approvedOrg}")
        page++
        adapter.submitList(viewModel.currentState.approvedOrg.approvedOrg)
        binding.acceptedOrgList.visibility = View.VISIBLE
        initScrollListener()
    }

    private fun loadMorePosts() {
        if (adapter.itemCount >= page * 10) {
            viewModel.getApprovedOrg(page)
        }
    }

    //마지막 item에서 스크롤 하면 로딩과 함께 다시 받아서 추가하기
    private fun initScrollListener() {
        binding.acceptedOrgList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.acceptedOrgList.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.acceptedOrgList.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadMorePosts()
                }
            }
        })
    }
}