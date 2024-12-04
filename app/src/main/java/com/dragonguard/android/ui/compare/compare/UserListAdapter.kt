package com.dragonguard.android.ui.compare.compare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.databinding.LanguageListBinding

class UserListAdapter(private val type: Int, private val listener: OnUserClickListener) :
    ListAdapter<GitRepoMember, UserListAdapter.ViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LanguageListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    inner class ViewHolder(private val binding: LanguageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GitRepoMember) {
            binding.languageText.text = data.github_id
            binding.languageText.setOnClickListener {
                when (type) {
                    1 -> listener.onFirstUserClick(data)

                    2 -> listener.onSecondUserClick(data)
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnUserClickListener {
        fun onFirstUserClick(data: GitRepoMember)
        fun onSecondUserClick(data: GitRepoMember)
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