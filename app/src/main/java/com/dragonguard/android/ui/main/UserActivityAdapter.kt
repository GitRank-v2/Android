package com.dragonguard.android.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.databinding.UserActivityListBinding

class UserActivityAdapter(
    private val datas: HashMap<String, Int>,
    private val type: List<String>,
) : RecyclerView.Adapter<UserActivityAdapter.ViewHolder>() {
    private lateinit var binding: UserActivityListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            UserActivityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(binding: UserActivityListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: Int?, type: String) {
            binding.contributeValue.text = data.toString()
            binding.contributeType.text = type
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[type[position % 4]], type[position % 4])
    }

}