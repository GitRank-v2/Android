package com.dragonguard.android.ui.search.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.databinding.ActivitySearchFilterBinding
import com.dragonguard.android.databinding.FilterSheetBinding
import com.dragonguard.android.ui.compare.compare.LanguagesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSheetFragment(
    context: Context,
    private val list: MutableList<String>,
    private val type: String,
    private val activityBinding: ActivitySearchFilterBinding
) : BottomSheetDialogFragment() {
    private lateinit var binding: FilterSheetBinding
    private var activityContext = context as SearchFilterActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilterSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filterTitle.text = type
        binding.filterItems.adapter = LanguagesAdapter(list, activityContext, type, activityBinding)
        binding.filterItems.layoutManager = LinearLayoutManager(requireContext())
        binding.filterItems.adapter?.notifyDataSetChanged()

    }
}