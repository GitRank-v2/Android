package com.dragonguard.android.ui.profile.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.R
import com.dragonguard.android.databinding.FragmentClientProfileBinding
import com.dragonguard.android.ui.main.MainActivity
import com.dragonguard.android.ui.menu.MenuActivity
import com.dragonguard.android.ui.profile.OthersReposAdapter
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientProfileFragment(
    private val userName: String
) : Fragment() {
    private val token = ""
    private lateinit var binding: FragmentClientProfileBinding
    private lateinit var orgAdapter: ClientGitOrgAdapter
    private lateinit var repoAdapter: OthersReposAdapter
    private val viewModel by viewModels<ClientProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientProfileBinding.inflate(inflater, container, false)
        val main = activity as MainActivity
        setHasOptionsMenu(true)
        main.setSupportActionBar(binding.toolbar)
        main.supportActionBar?.setDisplayShowTitleEnabled(false)
        main.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()

        viewModel.getClientDetail()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        initRecycler()
                    }
                }
            }
        }
    }


    private fun initRecycler() {
        Log.d(
            "결과",
            "사용자 org: ${viewModel.currentState.clientDetail.clientDetail.git_organizations}"
        )
        Log.d("결과", "사용자 repos: ${viewModel.currentState.clientDetail.clientDetail.git_repos}")
        if (!this@ClientProfileFragment.isDetached && this@ClientProfileFragment.isAdded) {
            orgAdapter = ClientGitOrgAdapter(
                viewModel.currentState.clientDetail.clientDetail.git_organizations,
                requireContext()
            )
            binding.memberOrganizaitonList.adapter = orgAdapter
            binding.memberOrganizaitonList.layoutManager = LinearLayoutManager(requireContext())
            orgAdapter.notifyDataSetChanged()

            repoAdapter = OthersReposAdapter(
                viewModel.currentState.clientDetail.clientDetail.git_repos,
                requireContext(),
                viewModel.currentState.clientDetail.clientDetail.member_profile_image,
                userName
            )
            binding.memberRepositoryList.adapter = repoAdapter
            binding.memberRepositoryList.layoutManager = LinearLayoutManager(requireContext())
            repoAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.setting, binding.toolbar.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_button -> {
                val intent = Intent(requireContext(), MenuActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}