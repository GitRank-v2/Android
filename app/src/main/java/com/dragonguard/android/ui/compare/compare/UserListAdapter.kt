package com.dragonguard.android.ui.compare.compare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.databinding.FragmentCompareUserBinding
import com.dragonguard.android.databinding.LanguageListBinding

class UserListAdapter(
    private val users: ArrayList<GitRepoMember>,
    private val userFragment: CompareUserFragment,
    private val type: Int,
    private val fragmentBinding: FragmentCompareUserBinding
) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LanguageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(private val binding: LanguageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.languageText.text = users[position].github_id
            binding.languageText.setOnClickListener {
                when (type) {
                    1 -> {
                        Glide.with(fragmentBinding.user1Profile).load(users[position].profile_url)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(fragmentBinding.user1Profile)
                        fragmentBinding.user1GithubId.text = users[position].github_id
                        users[position].github_id?.let {
                            userFragment.user1 = it
                        }
                        if (userFragment.user1 != "null" && userFragment.user2 != "null" && userFragment.user1.isNotBlank() && userFragment.user2.isNotBlank()) {
                            userFragment.initGraph()
                        }
                        userFragment.userGroup1.dismiss()
                    }

                    2 -> {
                        Glide.with(fragmentBinding.user2Profile).load(users[position].profile_url)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(fragmentBinding.user2Profile)
                        fragmentBinding.user2GithubId.text = users[position].github_id
                        userFragment.user2 = users[position].github_id.toString()
                        if (userFragment.user1 != "null" && userFragment.user2 != "null" && userFragment.user1.isNotBlank() && userFragment.user2.isNotBlank()) {
                            userFragment.initGraph()
                        }
                        userFragment.userGroup2.dismiss()
                    }
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}