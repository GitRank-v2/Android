package com.dragonguard.android.ui.compare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dragonguard.android.R
import com.dragonguard.android.databinding.FragmentCompareSearchBinding
import com.dragonguard.android.ui.compare.compare.RepoCompareActivity
import com.dragonguard.android.ui.compare.search.CompareSearchActivity

class SearchCompareRepoFragment : Fragment() {


    private lateinit var binding: FragmentCompareSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compare_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.repoCompare1.setOnClickListener {
            val intent = Intent(activity, CompareSearchActivity::class.java)
            intent.putExtra("count", FIRST_REPO_NUMBER)
            resultLauncher.launch(intent)
        }
        binding.repoCompare2.setOnClickListener {
            val intent = Intent(activity, CompareSearchActivity::class.java)
            intent.putExtra("count", SECOND_REPO_NUMBER)
            resultLauncher.launch(intent)
        }
        binding.repoChoose.setOnClickListener {
            if (binding.repoCompare1.text.isNullOrBlank() || binding.repoCompare2.text.isNullOrBlank() ||
                binding.repoCompare1.text == getString(R.string.repo1) || binding.repoCompare2.text == getString(
                    R.string.repo2
                )
            ) {
                Toast.makeText(requireContext(), "비교할 Repository를 선택해 주세요!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (binding.repoCompare1.text.toString() != binding.repoCompare2.text.toString()) {
                    val intent = Intent(requireContext(), RepoCompareActivity::class.java)
                    intent.putExtra("repo1", binding.repoCompare1.text.toString())
                    intent.putExtra("repo2", binding.repoCompare2.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "서로 다른 Repository를 선택해 주세요!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                1 -> {
                    binding.repoCompare1.text = result.data?.getStringExtra("repoName")
                }

                2 -> {
                    binding.repoCompare2.text = result.data?.getStringExtra("repoName")
                }
            }
        }


    companion object {
        private const val FIRST_REPO_NUMBER = 1
        private const val SECOND_REPO_NUMBER = 2
    }
}