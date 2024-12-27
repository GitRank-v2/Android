package com.dragonguard.android.ui.compare.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.databinding.RepositoryListBinding

//비교할 Repository를 나열하기 위한 recyclerview의 adapter
class SearchCompareRepoAdapter(count: Int, private val listener: OnItemClickListener) :
    ListAdapter<RepoSearchResultModel, SearchCompareRepoAdapter.ViewHolder>(differ) {
    private val repoCount = count
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RepositoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: RepositoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: RepoSearchResultModel, count: Int) {
            binding.repoName.text = data.full_name
            //binding.repoLanguage.text = data.language
            Log.d("name", data.full_name)
            //Log.d("count", "$repoCount")
            itemView.setOnClickListener {
                listener.onItemClick(count, data.full_name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), repoCount)
    }

    interface OnItemClickListener {
        fun onItemClick(count: Int, name: String)
    }


    companion object {
        private val differ = object : DiffUtil.ItemCallback<RepoSearchResultModel>() {
            override fun areItemsTheSame(
                oldItem: RepoSearchResultModel,
                newItem: RepoSearchResultModel
            ): Boolean {
                return oldItem.full_name == newItem.full_name && oldItem.description == newItem.description
            }

            override fun areContentsTheSame(
                oldItem: RepoSearchResultModel,
                newItem: RepoSearchResultModel
            ) = oldItem == newItem
        }
    }
}