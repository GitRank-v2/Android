package com.dragonguard.android.ui.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.databinding.FilterSheetBinding
import com.dragonguard.android.ui.compare.compare.LanguagesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSheetFragment(
    private val list: MutableList<String>,
    private val type: String,
    private val listener: LanguagesAdapter.OnChipClickListener
) : BottomSheetDialogFragment() {
    private lateinit var binding: FilterSheetBinding
    private val adapter by lazy { LanguagesAdapter(requireContext(), type, listener) }
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
        binding.filterItems.adapter = adapter
        binding.filterItems.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(list)
    }
}