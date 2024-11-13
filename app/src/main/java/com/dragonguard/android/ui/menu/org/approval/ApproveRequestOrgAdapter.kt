package com.dragonguard.android.ui.menu.org.approval

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.databinding.ApproveRequestListBinding

//승인 요청중인 조직 목록 adapter
class ApproveRequestOrgAdapter(
    private var datas: List<ApproveRequestOrgModelItem>,
    private val context: Context,
    private val token: String,
    private val viewModel: ApproveOrgViewModel,
    private val frag: ApproveOrgFragment
) : RecyclerView.Adapter<ApproveRequestOrgAdapter.ViewHolder>() {
    private var count = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ApproveRequestListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = datas.size

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: ApproveRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var current = 0
        fun bind(data1: ApproveRequestOrgModelItem, currentPosition: Int) {
            current = currentPosition
            binding.requestOrgName.text = data1.name
            binding.requestOrgType.text = data1.type
            binding.emailEndpoint.text = data1.email_endpoint
            binding.approveOrgBtn.setOnClickListener {
                approveApproval(data1, current)
                notifyDataSetChanged()
            }
            binding.rejectOrgBtn.setOnClickListener {
                rejectApproval(data1, current)
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun approveApproval(
        data1: ApproveRequestOrgModelItem,
        currentPosition: Int
    ) {
        viewModel.clickApprove(data1.id, currentPosition)
    }

    private fun rejectApproval(
        data1: ApproveRequestOrgModelItem,
        currentPosition: Int
    ) {
        viewModel.clickReject(data1.id, currentPosition)

    }

}