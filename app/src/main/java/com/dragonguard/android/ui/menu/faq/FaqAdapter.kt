package com.dragonguard.android.ui.menu.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.menu.FaqModel
import com.dragonguard.android.databinding.FaqListBinding

class FaqAdapter :
    ListAdapter<FaqModel, FaqAdapter.ViewHolder>(differ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FaqListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    inner class ViewHolder(private val binding: FaqListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FaqModel) {
            binding.faqTitle.text = data.title
            binding.faqAnswer.text = data.content
            binding.faqTitle.setOnClickListener {
                if (data.expandable) {
                    binding.ctChild.visibility = View.GONE
                    data.expandable = false
                } else {
                    binding.ctChild.visibility = View.VISIBLE
                    data.expandable = true
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

    companion object {
        private val differ = object : DiffUtil.ItemCallback<FaqModel>() {
            override fun areItemsTheSame(oldItem: FaqModel, newItem: FaqModel) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: FaqModel, newItem: FaqModel) =
                oldItem == newItem
        }
    }
}