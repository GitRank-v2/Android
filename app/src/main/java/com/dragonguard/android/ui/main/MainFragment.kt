package com.dragonguard.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.dragonguard.android.R
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.databinding.FragmentMainBinding
import com.dragonguard.android.ui.history.GitHistoryActivity
import com.dragonguard.android.ui.profile.other.OthersProfileActivity
import com.dragonguard.android.ui.search.SearchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainFragment(
    private var info: UserInfoModel,
    private val refresh: Boolean,
    private val viewModel: MainViewModel
) :
    Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val scope = CoroutineScope(Dispatchers.IO)

    //    private var menuItem: MenuItem? = null
    val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("mainCreate", "mainCreate")
        //setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        Log.d("token", info.toString())
//        val main = activity as MainActivity
//        main.setSupportActionBar(binding.toolbar)
//        main.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.toolbar.inflateMenu(R.menu.refresh)
        binding.githubProfile.clipToOutline = true
        initObserver()
        drawInfo()
        if (viewModel.currentState.repeatState.repeat.not()) {
            scope.launch {
                viewModel.setRepeat(true)
                while (true) {
                    Thread.sleep(3000)
                    handler.sendEmptyMessage(0)
                }
            }
        }

        binding.tokenFrame.setOnClickListener {
            val intent = Intent(requireActivity(), GitHistoryActivity::class.java)
            intent.putExtra("token", viewModel.currentState.newAccessToken.token)
            startActivity(intent)
        }
        binding.searchName.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            startActivity(intent)
        }
        binding.userId.setOnClickListener {
            Log.d("userIcon", "userIcon")
            val intent = Intent(requireActivity(), OthersProfileActivity::class.java)
            intent.putExtra("token", "")
            intent.putExtra("userName", info.github_id)
            startActivity(intent)
        }
        binding.githubProfile.setOnClickListener {
            Log.d("userIcon", "userIcon")
            val intent = Intent(requireActivity(), OthersProfileActivity::class.java)
            intent.putExtra("token", "")
            intent.putExtra("userName", info.github_id)
            startActivity(intent)
        }

    }

    private fun drawInfo() {
        val main = activity as MainActivity
        val layoutParams = binding.mainFrame.layoutParams as FrameLayout.LayoutParams
        layoutParams.bottomMargin = main.getNavSize()
        binding.mainFrame.layoutParams = layoutParams
        info.github_id?.let {
            binding.userId.text = info.github_id
        }
        if (!requireActivity().isFinishing) {
            Log.d("profile", "profile image ${info.profile_image}")
            if (refresh) {
                //프로필 사진 넣기
                /*val coroutine = CoroutineScope(Dispatchers.Main)
                coroutine.launch {
                    val deferred = coroutine.async(Dispatchers.IO) {
                        Glide.get(requireContext()).clearDiskCache()
                    }
                    val result = deferred.await()
                    Glide.with(this@MainFragment).load(info.profile_image)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .signature(
                            ObjectKey(
                                System.currentTimeMillis().toString()
                            )
                        )
                        .into(binding.githubProfile)
                }*/
            } else {
                /*Glide.with(this).load(info.profile_image)
                    .into(binding.githubProfile)*/

            }
        }

        when (info.tier) {
            "BRONZE" -> {
                binding.tierImg.setBackgroundResource(R.drawable.bronze)
            }

            "SILVER" -> {
                binding.tierImg.setBackgroundResource(R.drawable.silver)
            }

            "GOLD" -> {
                binding.tierImg.setBackgroundResource(R.drawable.gold)
            }

            "PLATINUM" -> {
                binding.tierImg.setBackgroundResource(R.drawable.platinum)
            }

            "DIAMOND" -> {
                binding.tierImg.setBackgroundResource(R.drawable.diamond)
            }
        }

        if (info.token_amount != null) {
            binding.tokenAmount.text = info.token_amount.toString()
        }
        val typeList = listOf("commits", "issues", "pullRequests", "review")
        if (info.organization != null) {
            binding.userOrgName.text = info.organization
        }
        val userActivity = HashMap<String, Int>()
        userActivity.put("commits", info.commits!!)
        userActivity.put("issues", info.issues!!)
        userActivity.put("pullRequests", info.pull_requests!!)
        info.reviews?.let {
            userActivity.put("review", it)
        }
        Log.d("map", "hashMap: $userActivity")
        binding.userUtil.adapter = UserActivityAdapter(userActivity, typeList)
        binding.userUtil.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        if (info.organization == null) {
            binding.mainOrgFrame.visibility = View.GONE
        } else {
            when (info.organization_rank) {
                1 -> {
                    when (info.member_github_ids?.size) {
                        1 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                        }

                        2 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                            binding.user3Githubid.text = info.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }

                        3 -> {
                            binding.user2Githubid.text = info.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = info.organization_rank!!.minus(1).toString()
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                            binding.user3Githubid.text = info.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }
                    }
                }

                else -> {
                    when (info.member_github_ids?.size) {
                        0 -> {
                            binding.user2Githubid.text = info.github_id
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = info.organization_rank.toString()
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                        }

                        1 -> {
                            binding.user2Githubid.text = info.member_github_ids!![1]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = info.organization_rank.toString()
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                        }

                        2 -> {
                            when (info.is_last) {
                                true -> {
                                    binding.user1Githubid.text = info.member_github_ids!![0]
                                    binding.user1Ranking.text =
                                        info.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = info.member_github_ids!![1]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = info.organization_rank.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                                }

                                false -> {
                                    binding.user2Githubid.text = info.member_github_ids!![1]
                                    binding.user2Ranking.text = info.organization_rank!!.toString()
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                                    binding.user3Githubid.text = info.member_github_ids!![1]
                                    binding.user3Ranking.text =
                                        info.organization_rank!!.plus(1).toString()

                                }

                                null -> {

                                }
                            }
                        }

                        3 -> {
                            when (info.is_last) {
                                true -> {
                                    binding.user1Githubid.text = info.member_github_ids!![1]
                                    binding.user1Ranking.text =
                                        info.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = info.member_github_ids!![2]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = info.organization_rank!!.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                                }

                                false -> {
                                    binding.user1Githubid.text = info.member_github_ids!![0]
                                    binding.user1Ranking.text =
                                        info.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = info.member_github_ids!![1]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = info.organization_rank.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                                    binding.user3Githubid.text = info.member_github_ids!![2]
                                    binding.user3Ranking.text =
                                        info.organization_rank!!.plus(1).toString()
                                }

                                null -> {

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initObserver() {

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.refresh, binding.toolbar.menu)
//        menuItem = menu.findItem(R.id.refresh_button)
//        menuItem?.icon?.alpha = 64
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.refresh_button -> {
//                if(refresh) {
//                    refresh = false
//                    val coroutine = CoroutineScope(Dispatchers.Main)
//                    coroutine.launch {
//                        if(this@MainFragment.isResumed) {
//                            Log.d("refresh", "refresh main!!")
//                            val resultRepoDeferred = coroutine.async(Dispatchers.IO) {
//                                viewmodel.updateUserInfo(token)
//                            }
//                            val resultRepo = resultRepoDeferred.await()
//                            info = resultRepo
//                            drawInfo()
//                        }
//                    }
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
    fun clearView() {
        if (this@MainFragment.isAdded && !this@MainFragment.isDetached && this@MainFragment.isVisible && !this@MainFragment.isRemoving) {
            binding.githubProfile.setImageResource(0)
            binding.userId.text = ""
            binding.tierImg.setImageResource(0)
            binding.tokenAmount.text = ""
            binding.userUtil.adapter = null
        }

    }

    override fun onDestroy() {
        //viewModel.setRepeat(false)
        super.onDestroy()
    }

    private fun setPage() {
        binding.userUtil.setCurrentItem((binding.userUtil.currentItem + 1) % 4, false)
    }

}