package com.dragonguard.android.ui.menu.org.approval

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.databinding.FragmentApproveOrgBinding
import com.dragonguard.android.util.LoadState
import kotlinx.coroutines.launch


class ApproveOrgFragment : Fragment() {
    private lateinit var binding: FragmentApproveOrgBinding
    private lateinit var viewModel: ApproveOrgViewModel
    var page = 0
    private var count = 0
    private var position = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_approve_org, container, false)
        viewModel = ApproveOrgViewModel()
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
                    if (state.loadState == LoadState.SUCCESS) {
                        initRecycler()
                    }

                    if (state.approveOrg.status) {
                        Toast.makeText(requireContext(), "승인됨", Toast.LENGTH_SHORT).show()
                        viewModel.currentState.requestedOrg.org.removeAt(position)
                        binding.waitingOrgList.adapter?.notifyDataSetChanged()
                        viewModel.resetClick()
                    }

                    if (state.rejectOrg.status) {
                        Toast.makeText(requireContext(), "반려됨", Toast.LENGTH_SHORT).show()
                        viewModel.currentState.requestedOrg.org.removeAt(position)
                        binding.waitingOrgList.adapter?.notifyDataSetChanged()
                        viewModel.resetClick()
                    }


                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        viewModel.getRequestedOrg(page)
    }


    private fun initRecycler() {
        Log.d("count", "count: $count")
        if (page == 0) {
            val adapter =
                ApproveRequestOrgAdapter(
                    viewModel.currentState.requestedOrg.org,
                    requireContext(),
                    viewModel.currentState.token.token,
                    viewModel,
                    this
                )
            binding.waitingOrgList.adapter = adapter
            binding.waitingOrgList.layoutManager = LinearLayoutManager(requireContext())
            adapter.notifyDataSetChanged()
            binding.waitingOrgList.visibility = View.VISIBLE
        }
        page++
        count = 0
        binding.waitingOrgList.adapter?.notifyDataSetChanged()
        initScrollListener()
    }

    private fun loadMorePosts() {
        if (page != 0) {
            viewModel.getRequestedOrg(page)
        }
    }

    //마지막 item에서 스크롤 하면 로딩과 함께 다시 받아서 추가하기
    private fun initScrollListener() {
        binding.waitingOrgList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.waitingOrgList.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.waitingOrgList.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadMorePosts()
                }
            }
        })
    }
}