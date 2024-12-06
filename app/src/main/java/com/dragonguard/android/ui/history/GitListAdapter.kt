package com.dragonguard.android.ui.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.klip.TokenHistoryModelItem
import com.dragonguard.android.databinding.TokenListBinding


class GitListAdapter(private val listener: OnHistoryClickListener) :
    ListAdapter<TokenHistoryModelItem, GitListAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TokenListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: TokenListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: TokenHistoryModelItem) {
            val parts = data.created_at!!.split("T")
            val textToShow = "${parts[0]} ${parts[1]}"
            Log.d("parts", "나누기 $parts")
            Log.d("parts", "합치기 $textToShow")
            binding.createdDate.text = textToShow.split(".")[0]
            binding.tokenType.text = data.contribute_type
            binding.tokenAmount.text = data.amount.toString()
            binding.tokenHistoryFrame.setOnClickListener {
                listener.onHistoryClick(data.transaction_hash_url)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnHistoryClickListener {
        fun onHistoryClick(url: String?)
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<TokenHistoryModelItem>() {
            override fun areItemsTheSame(
                oldItem: TokenHistoryModelItem,
                newItem: TokenHistoryModelItem
            ) = oldItem.created_at == newItem.created_at

            override fun areContentsTheSame(
                oldItem: TokenHistoryModelItem,
                newItem: TokenHistoryModelItem
            ) = oldItem == newItem
        }
    }
}