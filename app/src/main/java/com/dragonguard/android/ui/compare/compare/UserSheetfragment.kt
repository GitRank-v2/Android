package com.dragonguard.android.ui.compare.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.databinding.FragmentCompareUserBinding
import com.dragonguard.android.databinding.UserSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UserSheetfragment(
    private val context: CompareUserFragment,
    private val firstList: ArrayList<GitRepoMember>,
    private val secondList: ArrayList<GitRepoMember>,
    private val type: Int,
    private val repo1: String,
    private val repo2: String,
    private val fragmentBinding: FragmentCompareUserBinding
) : BottomSheetDialogFragment() {
    private lateinit var binding: UserSheetBinding
    private val firstFilterAdapter by lazy { UserListAdapter(type, context) }
    private val secondFilterAdapter by lazy { UserListAdapter(type, context) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filterTitleFirst.text = repo1
        binding.filterTitleSecond.text = repo2
        binding.firstFilterItems.adapter = firstFilterAdapter
        binding.firstFilterItems.layoutManager = LinearLayoutManager(requireContext())
        firstFilterAdapter.submitList(firstList)


        binding.secondFilterItems.adapter = secondFilterAdapter
        binding.secondFilterItems.layoutManager = LinearLayoutManager(requireContext())
        secondFilterAdapter.submitList(secondList)
    }

}