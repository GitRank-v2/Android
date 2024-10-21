package com.dragonguard.android.ui.compare.search

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.databinding.RepositoryListBinding

//비교할 Repository를 나열하기 위한 recyclerview의 adapter
class SearchCompareRepoAdapter(
    private val datas: ArrayList<RepoSearchResultModel>,
    private val context: Context,
    count: Int,
    private val token: String,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SearchCompareRepoAdapter.ViewHolder>() {
    private val repoCount = count
    private lateinit var binding: RepositoryListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RepositoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = datas.size

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //클릭리스너 구현
        fun bind(data: RepoSearchResultModel, count: Int) {
            val mContext = context as CompareSearchActivity
            binding.repoName.text = data.name
            //binding.repoLanguage.text = data.language
            Log.d("name", "$data.name")
            Log.d("count", "$repoCount")
            itemView.setOnClickListener {
                val resultIntent = Intent()
                resultIntent.putExtra("repoName", data.name)
                context.setResult(repoCount, resultIntent)
                context.finish()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], repoCount)
    }

    interface OnItemClickListener {
        fun onItemClick(count: Int, name: String)
    }

}