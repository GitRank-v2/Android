package com.dragonguard.android.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.dragonguard.android.R
import com.dragonguard.android.databinding.FragmentMainBinding
import com.dragonguard.android.ui.history.GitHistoryActivity
import com.dragonguard.android.ui.profile.other.OthersProfileActivity
import com.dragonguard.android.ui.search.SearchActivity
import com.dragonguard.android.util.CustomGlide
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment(
    private val viewModel: MainViewModel
) : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val userActivity = HashMap<String, Int>()

    private val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("mainCreate", "mainCreate")
        //setHasOptionsMenu(true)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            startActivity(intent)
        }
        binding.searchName.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            startActivity(intent)
        }
        binding.userId.setOnClickListener {
            Log.d("userIcon", "userIcon")
            val intent = Intent(requireActivity(), OthersProfileActivity::class.java)
            intent.putExtra("userName", viewModel.currentState.userInfo.userInfo.github_id)
            intent.putExtra("isUser", true)
            startActivity(intent)
        }
        binding.githubProfile.setOnClickListener {
            Log.d("userIcon", "userIcon")
            val intent = Intent(requireActivity(), OthersProfileActivity::class.java)
            intent.putExtra("userName", viewModel.currentState.userInfo.userInfo.github_id)
            intent.putExtra("isUser", true)
            startActivity(intent)
        }

    }

    private fun drawInfo() {
        val main = activity as MainActivity
        val layoutParams = binding.mainFrame.layoutParams as FrameLayout.LayoutParams
        layoutParams.bottomMargin = main.getNavSize()
        binding.mainFrame.layoutParams = layoutParams
        val infos = viewModel.currentState.userInfo.userInfo
        infos.github_id.let {
            binding.userId.text = infos.github_id
        }

        when (infos.tier) {
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

        binding.tokenAmount.text =
            String.format(
                getString(R.string.activity_sum),
                infos.contribution_amount
            )
        val typeList = listOf("COMMIT", "ISSUE", "PULL REQUEST", "REVIEW")
        if (infos.organization != null) {
            binding.userOrgName.text = infos.organization
        }
        Log.d("info", "info: $infos")

        userActivity.put("COMMIT", infos.commits)
        userActivity.put("ISSUE", infos.issues)
        userActivity.put("PULL REQUEST", infos.pull_requests)
        userActivity.put("REVIEW", infos.reviews)
        Log.d("map", "hashMap: $userActivity")
        binding.userUtil.adapter = UserActivityAdapter(userActivity, typeList)
        binding.userUtil.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        if (infos.organization.isNullOrEmpty()) {
            binding.mainOrgFrame.visibility = View.GONE
        } else {
            when (infos.organization_rank) {
                1 -> {
                    when (infos.member_github_ids?.size) {
                        1 -> {
                            binding.user2Githubid.text = infos.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                        }

                        2 -> {
                            binding.user2Githubid.text = infos.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                            binding.user3Githubid.text = infos.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }

                        3 -> {
                            binding.user2Githubid.text = infos.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text =
                                infos.organization_rank!!.minus(1).toString()
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                            binding.user3Githubid.text = infos.member_github_ids!![1]
                            binding.user3Ranking.text = "2"
                        }
                    }
                }

                else -> {
                    when (infos.member_github_ids?.size) {
                        0 -> {
                            binding.user2Githubid.text = infos.github_id
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                        }

                        1 -> {
                            binding.user2Githubid.text = infos.member_github_ids!![0]
                            binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                            binding.user2Ranking.text = "1"
                            binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                        }

                        2 -> {
                            when (infos.is_last) {
                                true -> {
                                    binding.user1Githubid.text = infos.member_github_ids!![0]
                                    binding.user1Ranking.text =
                                        infos.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = infos.member_github_ids!![1]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = infos.organization_rank.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                                }

                                false -> {
                                    binding.user2Githubid.text = infos.member_github_ids!![1]
                                    binding.user2Ranking.text = infos.organization_rank!!.toString()
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                                    binding.user3Githubid.text = infos.member_github_ids!![1]
                                    binding.user3Ranking.text =
                                        infos.organization_rank!!.plus(1).toString()

                                }

                                null -> {

                                }
                            }
                        }

                        3 -> {
                            when (infos.is_last) {
                                true -> {
                                    binding.user1Githubid.text = infos.member_github_ids!![1]
                                    binding.user1Ranking.text =
                                        infos.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = infos.member_github_ids!![2]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = infos.organization_rank!!.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)
                                }

                                false -> {
                                    binding.user1Githubid.text = infos.member_github_ids!![0]
                                    binding.user1Ranking.text =
                                        infos.organization_rank!!.minus(1).toString()

                                    binding.user2Githubid.text = infos.member_github_ids!![1]
                                    binding.user2Githubid.setTextAppearance(R.style.mainRanking)
                                    binding.user2Ranking.text = infos.organization_rank.toString()
                                    binding.user2Ranking.setTextAppearance(R.style.mainRanking)

                                    binding.user3Githubid.text = infos.member_github_ids!![2]
                                    binding.user3Ranking.text =
                                        infos.organization_rank!!.plus(1).toString()
                                }

                                null -> {

                                }
                            }
                        }
                    }
                }
            }
        }

        if (!requireActivity().isFinishing) {
            if (binding.mainFrame.visibility == View.INVISIBLE) {
                Log.d("profile image", "profile image ${infos.profile_image}")
                CustomGlide.drawImage(binding.githubProfile, infos.profile_image) {
                    binding.mainFrame.visibility = View.VISIBLE
                    viewModel.profileImageLoaded()
                }
            }

        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        Log.d("success UserInfo", "success")
                        viewModel.setFinish()
                        drawInfo()
                    }
                    if (state.loadState == LoadState.REFRESH) {
                        Log.d("refresh", "refresh main!!")
                        state.refreshAmount.amount.forEach { activity ->
                            userActivity[activity.contribute_type] = activity.amount.toInt()
                            if (activity.contribute_type == "CODE_REVIEW") viewModel.getUserInfo()
                        }
                    }
                }
            }
        }
    }

    fun clearView() {
        if (this@MainFragment.isAdded && !this@MainFragment.isDetached && this@MainFragment.isVisible && !this@MainFragment.isRemoving) {
            binding.githubProfile.setImageResource(0)
            binding.userId.text = ""
            binding.tierImg.setImageResource(0)
            binding.tokenAmount.text = ""
            binding.userUtil.adapter = null
        }

    }

    private fun ViewPager2.setCurrentWithDuration(item: Int, duration: Long) {
        val pxTopDrag = if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {  // 가로 방향인 경우
            width * (item - currentItem)
        } else {  // 세로 방향인 경우
            height * (item - currentItem)
        }
        val animator = ValueAnimator.ofInt(0, pxTopDrag)

        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            var previousValue = 0
            override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                val currentValue = valueAnimator.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
        })

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                beginFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
            }

            override fun onAnimationRepeat(animation: Animator) {
                super.onAnimationRepeat(animation)
            }
        })

        animator.interpolator = LinearInterpolator()
        animator.duration = duration
        animator.start()
    }

    private fun setPage() {
        if ((binding.userUtil.currentItem + 1) % 4 == 0) binding.userUtil.setCurrentItem(0, false)
        else binding.userUtil.setCurrentWithDuration((binding.userUtil.currentItem + 1) % 4, 250)

    }
}