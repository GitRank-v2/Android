package com.dragonguard.android.ui.menu.org.approval

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.databinding.ApproveRequestListBinding

//승인 요청중인 조직 목록 adapter
class ApproveRequestOrgAdapter(
    private val listener: ItemClickListener
) : ListAdapter<ApproveRequestOrgModelItem, ApproveRequestOrgAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ApproveRequestListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


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
                approveApproval(data1, current, true)

            }
            binding.rejectOrgBtn.setOnClickListener {
                approveApproval(data1, current, false)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun approveApproval(
        data1: ApproveRequestOrgModelItem,
        currentPosition: Int,
        isApproved: Boolean
    ) {
        listener.onClick(data1, currentPosition, isApproved)
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<ApproveRequestOrgModelItem>() {
            override fun areItemsTheSame(
                oldItem: ApproveRequestOrgModelItem,
                newItem: ApproveRequestOrgModelItem
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ApproveRequestOrgModelItem,
                newItem: ApproveRequestOrgModelItem
            ) = oldItem == newItem
        }
    }

}

interface ItemClickListener {
    fun onClick(data: ApproveRequestOrgModelItem, position: Int, isApproved: Boolean)
}