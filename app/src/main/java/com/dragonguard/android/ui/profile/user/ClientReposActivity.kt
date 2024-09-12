package com.dragonguard.android.ui.profile.user

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityClientReposBinding
import com.dragonguard.android.ui.profile.OthersReposAdapter
import kotlinx.coroutines.launch

class ClientReposActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientReposBinding
    private lateinit var viewModel: ClientReposViewModel
    private var orgName = ""
    private lateinit var reposAdapter: OthersReposAdapter
    private var img = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientReposBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ClientReposViewModel()
        initObserver()

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        intent.getStringExtra("orgName")?.let {
            orgName = it
        }
        intent.getStringExtra("img")?.let {
            img = it
        }
        if (orgName.isNotBlank() && img.isNotBlank()) {
            //viewModel.getGithubOrgRepos(orgName)
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state.loadState) {
                        is ClientReposContract.ClientReposState.LoadState.Success -> {
                            initRecycler()
                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }


    private fun initRecycler() {
        reposAdapter = OthersReposAdapter(
            viewModel.currentState.githubOrgRepos.githubOrgRepos.git_repos,
            this,
            img,
            orgName
        )
        binding.memberRepositoryList.adapter = reposAdapter
        binding.memberRepositoryList.layoutManager = LinearLayoutManager(this)
        reposAdapter.notifyDataSetChanged()
    }


    //    뒤로가기, 홈으로 화면전환 기능
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}