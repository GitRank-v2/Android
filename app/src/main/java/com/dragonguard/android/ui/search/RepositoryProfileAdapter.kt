package com.dragonguard.android.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.data.model.search.UserNameModelItem
import com.dragonguard.android.databinding.RepositoryListBinding

//검색한 레포지토리 나열하는 리사이클러뷰 어댑터 구현
class RepositoryProfileAdapter(
    private val datas: ArrayList<*>,
    private val imgList: HashMap<String, Int>,
    private val repoCount: Int,
    private val listener: OnRepositoryClickListener
) : RecyclerView.Adapter<RepositoryProfileAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RepositoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = datas.size

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: RepositoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: Any?) {
            when (data) {
                is RepoSearchResultModel -> {
                    binding.repoName.text = data.name
                    Log.d("name", "${data.name}")
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
                                listener.onSearchRepositoryClick(data.name)
                            }

                            else -> {
                                listener.onCompareSearchResultRepositoryClick(data.name)

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

    interface OnRepositoryClickListener {
        fun onSearchRepositoryClick(repoName: String)
        fun onCompareSearchResultRepositoryClick(repoName: String)
        fun onUserNameSearchClick(userName: String)
        fun onUserNotServiceMemberClick(userName: String)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

}