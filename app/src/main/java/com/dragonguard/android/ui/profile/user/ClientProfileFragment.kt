package com.dragonguard.android.ui.profile.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.data.model.detail.GitOrganization
import com.dragonguard.android.databinding.FragmentClientProfileBinding
import com.dragonguard.android.ui.menu.MenuActivity
import com.dragonguard.android.ui.profile.OthersReposAdapter
import com.dragonguard.android.ui.search.repo.RepoContributorsActivity
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientProfileFragment(
    private val userName: String
) : Fragment(), OthersReposAdapter.OnRepoClickListener,
    ClientGitOrgAdapter.OnOrganizationClickListener {
    private lateinit var binding: FragmentClientProfileBinding
    private lateinit var orgAdapter: ClientGitOrgAdapter
    private lateinit var repoAdapter: OthersReposAdapter
    private val viewModel by viewModels<ClientProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()

        viewModel.getClientDetail()
        binding.setting.setOnClickListener {
            Intent(requireContext(), MenuActivity::class.java).run { startActivity(this) }
        }
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
            orgAdapter = ClientGitOrgAdapter(this)
            binding.memberOrganizaitonList.adapter = orgAdapter
            binding.memberOrganizaitonList.layoutManager = LinearLayoutManager(requireContext())
            orgAdapter.notifyDataSetChanged()

            repoAdapter = OthersReposAdapter(
                viewModel.currentState.clientDetail.clientDetail.member_profile_image,
                userName,
                this
            )
            binding.memberRepositoryList.adapter = repoAdapter
            binding.memberRepositoryList.layoutManager = LinearLayoutManager(requireContext())
            repoAdapter.submitList(viewModel.currentState.clientDetail.clientDetail.git_repos)
        }
    }

    override fun onRepoClick(repoName: String) {
        Intent(requireContext(), RepoContributorsActivity::class.java).apply {
            putExtra("repoName", repoName)
        }.run { startActivity(this) }
    }

    override fun onOrganizationClick(organization: GitOrganization) {
        Intent(requireContext(), ClientReposActivity::class.java).apply {
            putExtra("orgName", organization.name)
            putExtra("img", organization.profile_image)
        }.run { startActivity(this) }
    }
}