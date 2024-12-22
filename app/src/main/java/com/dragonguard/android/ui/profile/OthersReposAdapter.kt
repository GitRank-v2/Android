package com.dragonguard.android.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dragonguard.android.databinding.OthersReposListBinding

class OthersReposAdapter(
    private val img: String,
    private val userName: String,
    private val listener: OnRepoClickListener
) : ListAdapter<String, OthersReposAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            OthersReposListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: OthersReposListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: String) {
            binding.reposFrame.clipToOutline = true
            binding.repoName.text = data
            Glide.with(binding.othersProfileImg).load(img).into(binding.othersProfileImg)
            binding.othersProfileImg.clipToOutline = true
            binding.userName.text = userName
            binding.repoContributeImg.setOnClickListener {
                listener.onRepoClick(data)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnRepoClickListener {
        fun onRepoClick(repoName: String)
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
        }
    }
}