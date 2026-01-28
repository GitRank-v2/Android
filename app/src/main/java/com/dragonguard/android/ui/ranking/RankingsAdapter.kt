package com.dragonguard.android.ui.ranking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.rankings.OrgInternalRankingsModel
import com.dragonguard.android.data.model.rankings.TotalOrganizationModel
import com.dragonguard.android.data.model.rankings.TotalUsersRankingsModel
import com.dragonguard.android.databinding.RankingListBinding
import com.dragonguard.android.util.CustomGlide

class RankingsAdapter(
    private val listener: OnRankingClickListener
) : ListAdapter<Any, RankingsAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RankingListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(private val binding: RankingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data1: Any?) {
            binding.eachProfile.clipToOutline = true
            when (data1) {
                is TotalUsersRankingsModel -> {
                    binding.eachRanking.text = data1.ranking.toString()
                    CustomGlide.drawImage(binding.eachProfile, data1.profile_image) {}
                    binding.rankingGithubId.text = data1.github_id
                    binding.rankingContribute.text = data1.contribution_amount.toString()
                    when (data1.tier) {
                        "BRONZE" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_bronze)
                        }

                        "SILVER" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_silver)
                        }

                        "GOLD" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_gold)
                        }

                        "PLATINUM" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_platinum)
                        }

                        "DIAMOND" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_diamond)
                        }

                        else -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_unrank)
                        }
                    }
                    binding.rankingItem.setOnClickListener {
                        listener.onUserRankingClick(data1.github_id!!)
                    }
                }

                is OrgInternalRankingsModel -> {
                    Log.d("image", "profile_img : ${data1.profile_image}")
                    if (data1.profile_image.isNullOrBlank()) {
                        binding.profileLink.visibility = View.GONE
                    } else {
                        binding.profileLink.visibility = View.VISIBLE
                    }

                    binding.eachRanking.text = data1.ranking.toString()
                    CustomGlide.drawImage(binding.eachProfile, data1.profile_image) {}
                    binding.rankingGithubId.text = data1.github_id
                    binding.rankingContribute.text = data1.tokens.toString()
                    binding.rankingItem.setOnClickListener {
                        if (data1.profile_image.isNullOrBlank()) {
                            listener.onOrgInternalRankingClick(data1.github_id!!)
                        } else {
                            listener.onOrgInternalRankingUserClick(data1.github_id!!)
                        }
                    }
                    when (data1.tier) {
                        "BRONZE" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_bronze)
                        }

                        "SILVER" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_silver)
                        }

                        "GOLD" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_gold)
                        }

                        "PLATINUM" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_platinum)
                        }

                        "DIAMOND" -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_diamond)
                        }

                        else -> {
                            binding.rankerContent.setBackgroundResource(R.drawable.shadow_unrank)
                        }
                    }
                }

                is TotalOrganizationModel -> {
                    binding.profileLink.visibility = View.GONE
                    binding.eachRanking.text = data1.ranking.toString()
                    binding.rankingGithubId.text = data1.name
                    when (data1.type) {
                        "COMPANY" -> {
                            binding.eachProfile.setImageResource(R.drawable.company)
                        }

                        "UNIVERSITY" -> {
                            binding.eachProfile.setImageResource(R.drawable.university)
                        }

                        "HIGH_SCHOOL" -> {
                            binding.eachProfile.setImageResource(R.drawable.high_school)
                        }

                        else -> {

                        }
                    }
                    binding.rankingContribute.text = data1.contribution_amount.toString()
                    binding.rankingItem.setOnClickListener {
                        listener.onOrgRankingClick(data1.name!!)
                    }
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnRankingClickListener {
        fun onUserRankingClick(userName: String)
        fun onOrgInternalRankingClick(orgName: String)
        fun onOrgInternalRankingUserClick(userName: String)
        fun onOrgRankingClick(orgName: String)
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any) =
                when (oldItem) {
                    is TotalUsersRankingsModel -> {
                        if (newItem !is TotalUsersRankingsModel) false
                        else oldItem.github_id == newItem.github_id
                    }

                    is OrgInternalRankingsModel -> {
                        if (newItem !is OrgInternalRankingsModel) false
                        else oldItem.github_id == newItem.github_id
                    }

                    is TotalOrganizationModel -> {
                        if (newItem !is TotalOrganizationModel) false
                        else oldItem.name == newItem.name
                    }

                    else -> {
                        false
                    }
                }

            override fun areContentsTheSame(oldItem: Any, newItem: Any) =
                oldItem == newItem
        }
    }
}