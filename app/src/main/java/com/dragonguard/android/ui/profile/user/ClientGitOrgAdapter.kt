package com.dragonguard.android.ui.profile.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.detail.GitOrganization
import com.dragonguard.android.databinding.GitOrganizationListBinding
import com.dragonguard.android.util.CustomGlide

class ClientGitOrgAdapter(private val listener: OnOrganizationClickListener) :
    ListAdapter<GitOrganization, ClientGitOrgAdapter.ViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GitOrganizationListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: GitOrganizationListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: GitOrganization) {
            CustomGlide.drawImage(binding.gitOrganizationProfile, data.profile_image) {}

            binding.gitOrganizationName.text = data.name
            binding.gitOrgImg.setOnClickListener {
                listener.onOrganizationClick(data)
            }
        }
    }

    interface OnOrganizationClickListener {
        fun onOrganizationClick(organization: GitOrganization)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<GitOrganization>() {
            override fun areItemsTheSame(oldItem: GitOrganization, newItem: GitOrganization) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: GitOrganization, newItem: GitOrganization) =
                oldItem == newItem
        }
    }
}