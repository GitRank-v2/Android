package com.dragonguard.android.ui.profile.other

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.R
import com.dragonguard.android.data.model.detail.UserProfileModel
import com.dragonguard.android.databinding.ActivityUserProfileBinding
import com.dragonguard.android.ui.profile.OthersReposAdapter
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OthersProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var name = ""
    private var isUser = false
    private lateinit var othersReposAdapter: OthersReposAdapter
    private val viewModel by viewModels<OthersProfileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()

        intent.getStringExtra("userName")?.let {
            name = it
        }

        intent.getBooleanExtra("isUser", false).let {
            isUser = it
        }

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.setTitle(name)
        binding.profileImg.clipToOutline = true
        if (isUser) {
            viewModel.getUserProfile()
        } else {
            viewModel.getOthersProfile(name)
        }

    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        state.userProfile.userProfile.let {
                            initRecycler(it)
                        }
                    }
                }
            }
        }
    }

    private fun initRecycler(result: UserProfileModel) {
        othersReposAdapter =
            OthersReposAdapter(
                result.git_repos,
                this,
                result.profile_image,
                name
            )
        binding.userRepoList.adapter = othersReposAdapter
        binding.userRepoList.layoutManager = LinearLayoutManager(this)
        binding.userRepoList.visibility = View.VISIBLE
        othersReposAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}