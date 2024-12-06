package com.dragonguard.android.ui.menu.org.approval

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.org.ApproveRequestOrgModelItem
import com.dragonguard.android.databinding.ApprovedOrgListBinding

//승인된 조직 목록 adapter
class ApprovedOrgAdapter :
    ListAdapter<ApproveRequestOrgModelItem, ApprovedOrgAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ApprovedOrgListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: ApprovedOrgListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data1: ApproveRequestOrgModelItem) {
            binding.emailEndpoint.text = data1.email_endpoint
            binding.approvedOrgName.text = data1.name
            binding.approvedOrgType.text = data1.type
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
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