package com.dragonguard.android.ui.compare.compare

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityRepoCompareBinding
import com.dragonguard.android.util.LoadState
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoCompareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepoCompareBinding
    private lateinit var adapter: CompareAdapter
    private var repo1 = ""
    private var repo2 = ""
    private val viewModel by viewModels<RepoCompareViewModel>()
    private lateinit var compareUserFragment: CompareUserFragment
    private lateinit var compareRepoFragment: CompareRepoFragment
    private var refresh = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repo_compare)
        initObserver()
        this.onBackPressedDispatcher.addCallback(this, callback)
        repo1 = intent.getStringExtra("repo1")!!
        repo2 = intent.getStringExtra("repo2")!!
        viewModel.requestCompareRepoMembers(repo1, repo2)
        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.title = "Repository 비교"

    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        if (state.repoMembers.repoMembers.first_result != null && state.repoMembers.repoMembers.second_result != null) {
                            startFragment()
                        }
                    }
                }
            }
        }
    }

    private fun startFragment() {
        Log.d("startFragment", "startFragment")
        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        binding.compareFrame.visibility = View.VISIBLE
        compareRepoFragment = CompareRepoFragment(repo1, repo2, viewModel)
        compareUserFragment = CompareUserFragment(
            repo1,
            repo2,
            viewModel.currentState.repoMembers.repoMembers.first_result!!,
            viewModel.currentState.repoMembers.repoMembers.second_result!!
        )
        adapter = CompareAdapter(this, viewModel.currentState.token.token)

        adapter.addFragment(compareRepoFragment)
        adapter.addFragment(compareUserFragment)

        binding.fragmentContent.adapter = adapter
        val tabs = arrayOf("Repository", "User")
        TabLayoutMediator(binding.compareTab, binding.fragmentContent) { tab, position ->
            tab.text = tabs[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh, binding.toolbar.menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.refresh_button -> {
                if (refresh) {
                    refresh = false

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}