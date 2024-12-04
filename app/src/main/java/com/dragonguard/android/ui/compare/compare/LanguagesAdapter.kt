package com.dragonguard.android.ui.compare.compare

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.databinding.LanguageListBinding
import com.google.android.material.chip.Chip

class LanguagesAdapter(
    private val context: Context,
    private val type: String,
    private val listener: OnChipClickListener
) :
    ListAdapter<String, LanguagesAdapter.ViewHolder>(differ) {

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
        fun bind(data: String) {
            Log.d("type", type)
            binding.languageText.text = data
            val chip = Chip(context)
            chip.chipBackgroundColor = ColorStateList.valueOf(Color.rgb(195, 202, 251))
            chip.setTextAppearanceResource(R.style.textAppearance)
            chip.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            chip.text = binding.languageText.text.toString()
            binding.languageText.setOnClickListener {
                if (binding.languageText.tag == "on") {
                    binding.languageText.tag = "off"
                    binding.languageText.setBackgroundResource(R.drawable.shadow_off)
                    when (type) {
                        "language" -> listener.onOffLanguageClick(
                            type,
                            binding.languageText.text.toString()
                        )

                        "stars" -> listener.onOffStarClick(
                            type,
                            binding.languageText.text.toString()
                        )

                        "forks" -> listener.onOffForkClick(
                            type,
                            binding.languageText.text.toString()
                        )

                        "topics" -> listener.onOffTopicClick(
                            type,
                            binding.languageText.text.toString()
                        )
                    }
                } else {
                    binding.languageText.tag = "on"
                    binding.languageText.setBackgroundResource(R.drawable.shadow_on)
                    when (type) {
                        "language" -> listener.onOnLanguageClick(
                            chip,
                            type,
                            binding.languageText.text.toString()
                        )

                        "stars" -> listener.onOnStarClick(chip, type)

                        "forks" -> listener.onOnForkClick(chip, type)

                        "topics" -> listener.onOnTopicClick(chip, type)
                    }
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
        private val differ = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
        }
    }

    interface OnChipClickListener {
        fun onOffLanguageClick(type: String, prev: String)
        fun onOffStarClick(type: String, prev: String)
        fun onOffForkClick(type: String, prev: String)
        fun onOffTopicClick(type: String, prev: String)

        fun onOnLanguageClick(chip: Chip, type: String, prev: String)
        fun onOnStarClick(chip: Chip, type: String)
        fun onOnForkClick(chip: Chip, type: String)
        fun onOnTopicClick(chip: Chip, type: String)
    }
}