package com.dragonguard.android.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.databinding.RepositoryListBinding

//검색한 레포지토리 나열하는 리사이클러뷰 어댑터 구현
class RepositoryProfileAdapter(
    private val imgList: HashMap<String, Int>,
    private val repoCount: Int,
    private val listener: OnRepositoryClickListener
) : ListAdapter<Any, RepositoryProfileAdapter.ViewHolder>(differ) {
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
        fun bind(data: Any?) {
            when (data) {
                is RepoSearchResultModel -> {
                    binding.repoName.text = data.full_name
                    Log.d("name", "${data.full_name}")
                    val img = imgList[data.language]
                    if (img != null) {
                        binding.langImg.setBackgroundResource(img)
                    }
                    data.created_at?.let {
                        val parts = it.split("T")
                        val textToShow = parts[0] + " " + parts[1]
                        val last = textToShow.replace("Z", "")
                        binding.repoCreateDate.text = last
                    }
                    binding.repoLanguage.text = data.language
                    itemView.setOnClickListener {
//                Toast.makeText(context, "${repoName.text} 눌림", Toast.LENGTH_SHORT).show()

                        Log.d("몇번", "현재 repoCount : $repoCount")
                        when (repoCount) {
                            0 -> {
                                listener.onSearchRepositoryClick(data.full_name)
                            }

                            else -> {
                                listener.onCompareSearchResultRepositoryClick(data.full_name)

                            }
                        }

                    }
                }

                is UserNameModelItem -> {
                    binding.repoName.text = data.github_id
                    itemView.setOnClickListener {
                        Log.d("users", "user = $data")
                        if (data.is_service_member) {
                            listener.onUserNameSearchClick(data.github_id)

                        } else {
                            listener.onUserNotServiceMemberClick(data.github_id)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(
                oldItem: Any,
                newItem: Any
            ) = when (oldItem) {
                is RepoSearchResultModel -> {
                    val new = newItem as RepoSearchResultModel
                    oldItem.compare(new)
                }

                is UserNameModelItem -> {
                    val new = newItem as UserNameModelItem
                    oldItem.compare(new)
                }

                else -> false
            }

            override fun areContentsTheSame(
                oldItem: Any,
                newItem: Any
            ) = oldItem == newItem
        }
    }

    interface OnRepositoryClickListener {
        fun onSearchRepositoryClick(repoName: String)
        fun onCompareSearchResultRepositoryClick(repoName: String)
        fun onUserNameSearchClick(userName: String)
        fun onUserNotServiceMemberClick(userName: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}