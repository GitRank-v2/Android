package com.dragonguard.android.ui.search.repo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.databinding.ContributorsListBinding
import com.dragonguard.android.util.CustomGlide

/*
 선택한 repo의 contributor들의 정보를 나열하기 위한 recycleradapter
 */
class ContributorsAdapter(
    private val colors: ArrayList<Int>,
    private val listener: OnRepoContributorClickListener
) :
    ListAdapter<GitRepoMember, ContributorsAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ContributorsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: ContributorsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data1: GitRepoMember) {
            binding.contributeRanking.text = data1.commits.toString()
            binding.contrubutorId.text = data1.github_id
            val red = (Math.random() * 255).toInt()
            val green = (Math.random() * 255).toInt()
            val blue = (Math.random() * 255).toInt()
//            binding.contributorColor.imageTintList = ColorStateList.valueOf(Color.rgb(red,green,blue))
            CustomGlide.drawImage(binding.contributorProfile, data1.profile_url) {}
            colors.add(Color.rgb(red, green, blue))
            binding.contributorProfile.clipToOutline = true
            binding.contributorsLayout.setOnClickListener {
                if (data1.is_service_member == true) {
                    listener.onRepoContributorClick(data1.github_id!!)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnRepoContributorClickListener {
        fun onRepoContributorClick(userName: String)
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<GitRepoMember>() {
            override fun areItemsTheSame(oldItem: GitRepoMember, newItem: GitRepoMember) =
                oldItem.github_id == newItem.github_id

            override fun areContentsTheSame(oldItem: GitRepoMember, newItem: GitRepoMember) =
                oldItem == newItem
        }
    }
}