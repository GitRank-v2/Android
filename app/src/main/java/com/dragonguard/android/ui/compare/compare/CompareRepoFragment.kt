package com.dragonguard.android.ui.compare.compare

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.data.model.compare.CompareRepoResponseModel
import com.dragonguard.android.data.model.compare.RepoStats
import com.dragonguard.android.databinding.FragmentCompareRepoBinding
import com.dragonguard.android.util.CustomGlide
import com.dragonguard.android.util.LoadState
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

//선택한 두 Repository를 비교하기 위한 fragment
@AndroidEntryPoint
class CompareRepoFragment(
    repoName1: String,
    repoName2: String,
    private val viewModel: RepoCompareViewModel
) : Fragment() {
    // TODO: Rename and change types of parameters
    private var repo1 = repoName1
    private var repo2 = repoName2
    private lateinit var binding: FragmentCompareRepoBinding
    private lateinit var repoCompareAdapter: RepoCompareAdapter
    private var count = 0
    private val compareItems = arrayListOf(
        "forks",
        "closed issues",
        "open issues",
        "stars",
        "subscribers",
        "watchers",
        "total commits",
        "max commits",
        "min commits",
        "contributors",
        "average commits",
        "total additions",
        "max addtions",
        "min additions",
        "adders",
        "average addtions",
        "total deletions",
        "max deletions",
        "min deletions",
        "deleters",
        "average deletions",
        "languages",
        "average lines"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCompareRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.repo1User1.clipToOutline = true
        binding.repo1User2.clipToOutline = true
        binding.repo1User3.clipToOutline = true
        binding.repo1User4.clipToOutline = true
        binding.repo2User1.clipToOutline = true
        binding.repo2User2.clipToOutline = true
        binding.repo2User3.clipToOutline = true
        binding.repo2User4.clipToOutline = true
        initObserver()
        viewModel.requestCompareRepo(repo1, repo2)
    }

    //activity 구성 이후 화면을 초기화하는 함수
    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.REPO_SUCCESS) {
                        checkRepos(state.repo.repo)
                    }
                }
            }
        }
    }


    /*
    비교하는 API의 결과의 이상유뮤 확인 후
    recyclerview 그리는 함수 호출
     */
    private fun checkRepos(result: CompareRepoResponseModel) {
        if (result.first != null && result.second != null) {
            try {
                result.first.git_repo!!
                result.first.statistics!!
                result.first.languages_stats!!
                result.first.languages!!
                result.second.git_repo!!
                result.second.statistics!!
                result.second.languages_stats!!
                result.second.languages!!
                initRecycler(result)

            } catch (e: Exception) {
                count++
                Log.d("checkRepos()", "error : $e")
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ viewModel.requestCompareRepo(repo1, repo2) }, 5000)
            }
        } else {
            count++
            Log.d("checkRepos()", "first: ${result.first} second: ${result.second}")
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ viewModel.requestCompareRepo(repo1, repo2) }, 5000)
        }
    }

    /*
    두 Repository를 비교하기 위한 표를 그리는 recyclerview
    결과에 문제 없으면 다 그리고 그래프를 그리는 함수 호출
     */
    private fun initRecycler(result: CompareRepoResponseModel) {
        viewModel.setFinish()
        Log.d("initRecycler()", "리사이클러뷰 구현 시작")
        if (result.first!!.languages_stats == null || result.second!!.languages_stats == null) {
            Log.d(
                "initRecycler()",
                "${result.first.languages_stats} 혹은 ${result.second!!.languages_stats}이 널입니다."
            )
            count++
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ viewModel.requestCompareRepo(repo1, repo2) }, 5000)
        } else {
            repoCompareAdapter = RepoCompareAdapter(result.first!!, result.second!!)
            binding.repoCompareList.adapter = repoCompareAdapter
            binding.repoCompareList.layoutManager = LinearLayoutManager(requireContext())
            repoCompareAdapter.submitList(compareItems)
            initGraph(result.first, result.second)
        }
    }

    /*
    두 Repository를 비교하기 위한 그래프를 그리는 함수
    가로로 슬라이딩하며 애니메이션 적용함
     */
    private fun initGraph(data1: RepoStats, data2: RepoStats) {
        data1.languages!!
        data1.git_repo!!
        data1.statistics!!
        data1.languages_stats!!
        data2.languages!!
        data2.git_repo!!
        data2.statistics!!
        data2.languages_stats!!
        val sum1 = data1.languages.values.sum()
        val sum2 = data2.languages.values.sum()
        val colors1 = ArrayList<Int>()
        val colors2 = ArrayList<Int>()
        var red1 = 0
        var green1 = 0
        var blue1 = 0
        var red2 = 0
        var green2 = 0
        var blue2 = 0
        var etc1: Float = 0f
        var etc2: Float = 0f
        val entries1 = ArrayList<PieEntry>()
        val legendEntry1 = HashMap<String, Int>()
        val legendEntry2 = HashMap<String, Int>()
        val entries2 = ArrayList<PieEntry>()

        data1.languages.forEach {
            if ((it.value.toFloat() / sum1) * 100 < 7) {
                etc1 += (it.value.toFloat() / sum1) * 100
            } else {
                entries1.add(PieEntry((it.value.toFloat() / sum1) * 100, it.key))
                red1 = (Math.random() * 255).toInt()
                green1 = (Math.random() * 255).toInt()
                blue1 = (Math.random() * 255).toInt()
                colors1.add(Color.rgb(red1, green1, blue1))
                legendEntry1[it.key] = Color.rgb(red1, green1, blue1)
            }
        }
        if (etc1 != 0f) {
            entries1.add(PieEntry(etc1, "etc"))
            colors1.add(Color.BLACK)
            legendEntry1["etc"] = Color.BLACK
        }

        legendEntry1.forEach {
            val linear = LinearLayout(requireContext())
            linear.orientation = LinearLayout.HORIZONTAL
            linear.gravity = Gravity.CENTER
            val param = LinearLayout.LayoutParams(30, 30)
            param.setMargins(5, 0, 0, 0)
            val color = View(requireContext())
            color.setBackgroundColor(it.value)
            color.layoutParams = param
            val text = TextView(requireContext())
            text.text = it.key
            text.textSize = 11f
            text.setTextColor(Color.BLACK)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 0, 10, 0)
            text.layoutParams = params
            linear.addView(color)
            linear.addView(text)
            binding.repo1Legend.addView(linear)
        }

        Log.d("entry", "legendEntry $legendEntry1")
        val dataSet1 = PieDataSet(entries1, data1.git_repo.full_name)
        dataSet1.label = null
        dataSet1.setDrawValues(true)
        dataSet1.valueTextSize = 12f
        dataSet1.colors = colors1
        dataSet1.valueFormatter = ScoreCustomFormatter()
        dataSet1.valueTextColor = Color.WHITE
        val firstData = PieData(dataSet1)
        binding.repo1Language.setTouchEnabled(true)
        binding.repo1Language.description.isEnabled = false
        binding.repo1Language.data = firstData
        binding.repo1Language.setDrawEntryLabels(true)
        binding.repo1Language.setEntryLabelColor(Color.BLACK)
        binding.repo1Language.legend.isEnabled = false
        binding.repo1Language.invalidate()
        binding.repo1Language.visibility = View.VISIBLE

        data2.languages!!.forEach {
            if ((it.value.toFloat() / sum2) * 100 < 7) {
                etc2 += (it.value.toFloat() / sum2) * 100
            } else {
                entries2.add(PieEntry((it.value.toFloat() / sum2) * 100, it.key))
                red2 = (Math.random() * 255).toInt()
                green2 = (Math.random() * 255).toInt()
                blue2 = (Math.random() * 255).toInt()
                colors2.add(Color.rgb(red2, green2, blue2))
                legendEntry2[it.key] = Color.rgb(red2, green2, blue2)
            }
        }
        if (etc2 != 0f) {
            entries2.add(PieEntry(etc2, "etc"))
            colors2.add(Color.BLACK)
            legendEntry2["etc"] = Color.BLACK
        }

        val dataSet2 = PieDataSet(entries2, data2.git_repo!!.full_name)
        dataSet2.label = null
        dataSet2.colors = colors2
        dataSet2.setDrawValues(true)
        dataSet2.valueFormatter = ScoreCustomFormatter()
        dataSet2.valueTextColor = Color.WHITE
        dataSet2.valueTextSize = 12f

        legendEntry2.forEach {
            val linear = LinearLayout(requireContext())
            linear.orientation = LinearLayout.HORIZONTAL
            linear.gravity = Gravity.CENTER
            val param = LinearLayout.LayoutParams(30, 30)
            param.setMargins(5, 0, 0, 0)
            val color = View(requireContext())
            color.setBackgroundColor(it.value)
            color.layoutParams = param
            val text = TextView(requireContext())
            text.text = it.key
            text.textSize = 11f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 0, 10, 0)
            text.layoutParams = params
            linear.addView(color)
            linear.addView(text)
            text.setTextColor(Color.BLACK)
            binding.repo2Legend.addView(linear)
        }

        val secondData = PieData(dataSet2)
        binding.repo2Language.setTouchEnabled(true)
        binding.repo2Language.description.isEnabled = false
        binding.repo2Language.data = secondData
        binding.repo2Language.legend.isEnabled = false
        binding.repo2Language.setDrawEntryLabels(true)
        binding.repo2Language.setEntryLabelColor(Color.BLACK)
        binding.repo2Language.invalidate()
        binding.repo2Language.visibility = View.VISIBLE
        initProfiles()
    }

    private fun initProfiles() {
        Log.d("frist", "first : ${viewModel.currentState.repoMembers.repoMembers.first}")
        Log.d("second", "second : ${viewModel.currentState.repoMembers.repoMembers.second}")
        when (viewModel.currentState.repoMembers.repoMembers.first!!.size) {
            0 -> {
                binding.repo1User1.visibility = View.INVISIBLE
                binding.repo1User2.visibility = View.INVISIBLE
                binding.repo1User3.visibility = View.INVISIBLE
                binding.repo1User4.visibility = View.INVISIBLE
            }

            1 -> {
                adaptProfile(
                    1,
                    viewModel.currentState.repoMembers.repoMembers.first!![0].profile_url
                )
                binding.repo1User2.visibility = View.INVISIBLE
                binding.repo1User3.visibility = View.INVISIBLE
                binding.repo1User4.visibility = View.INVISIBLE
            }

            2 -> {
                adaptProfile(
                    1,
                    viewModel.currentState.repoMembers.repoMembers.first!![0].profile_url
                )
                adaptProfile(
                    2,
                    viewModel.currentState.repoMembers.repoMembers.first!![1].profile_url
                )
                binding.repo1User3.visibility = View.INVISIBLE
                binding.repo1User4.visibility = View.INVISIBLE
            }

            3 -> {
                adaptProfile(
                    1,
                    viewModel.currentState.repoMembers.repoMembers.first!![0].profile_url
                )
                adaptProfile(
                    2,
                    viewModel.currentState.repoMembers.repoMembers.first!![1].profile_url
                )
                adaptProfile(
                    3,
                    viewModel.currentState.repoMembers.repoMembers.first!![2].profile_url
                )
                binding.repo1User4.visibility = View.INVISIBLE
            }

            else -> {
                adaptProfile(
                    1,
                    viewModel.currentState.repoMembers.repoMembers.first!![0].profile_url
                )
                adaptProfile(
                    2,
                    viewModel.currentState.repoMembers.repoMembers.first!![1].profile_url
                )
                adaptProfile(
                    3,
                    viewModel.currentState.repoMembers.repoMembers.first!![2].profile_url
                )
                adaptProfile(
                    4,
                    viewModel.currentState.repoMembers.repoMembers.first!![3].profile_url
                )
            }

        }

        when (viewModel.currentState.repoMembers.repoMembers.second!!.size) {
            0 -> {
                binding.repo2User1.visibility = View.INVISIBLE
                binding.repo2User2.visibility = View.INVISIBLE
                binding.repo2User3.visibility = View.INVISIBLE
                binding.repo2User4.visibility = View.INVISIBLE
            }

            1 -> {
                adaptProfile(
                    5,
                    viewModel.currentState.repoMembers.repoMembers.second!![0].profile_url
                )
                binding.repo2User2.visibility = View.INVISIBLE
                binding.repo2User3.visibility = View.INVISIBLE
                binding.repo2User4.visibility = View.INVISIBLE
            }

            2 -> {
                adaptProfile(
                    5,
                    viewModel.currentState.repoMembers.repoMembers.second!![0].profile_url
                )
                adaptProfile(
                    6,
                    viewModel.currentState.repoMembers.repoMembers.second!![1].profile_url
                )
                binding.repo2User3.visibility = View.INVISIBLE
                binding.repo2User4.visibility = View.INVISIBLE
            }

            3 -> {
                adaptProfile(
                    5,
                    viewModel.currentState.repoMembers.repoMembers.second!![0].profile_url
                )
                adaptProfile(
                    6,
                    viewModel.currentState.repoMembers.repoMembers.second!![1].profile_url
                )
                adaptProfile(
                    7,
                    viewModel.currentState.repoMembers.repoMembers.second!![2].profile_url
                )
                binding.repo2User4.visibility = View.INVISIBLE
            }

            else -> {
                adaptProfile(
                    5,
                    viewModel.currentState.repoMembers.repoMembers.second!![0].profile_url
                )
                adaptProfile(
                    6,
                    viewModel.currentState.repoMembers.repoMembers.second!![1].profile_url
                )
                adaptProfile(
                    7,
                    viewModel.currentState.repoMembers.repoMembers.second!![2].profile_url
                )
                adaptProfile(
                    8,
                    viewModel.currentState.repoMembers.repoMembers.second!![3].profile_url
                )
            }
        }

        binding.rankingLottie.pauseAnimation()
        binding.rankingLottie.visibility = View.GONE
        binding.repoCompareList.visibility = View.VISIBLE
        binding.repo1Name.text = repo1
        binding.repo2Name.text = repo2
        binding.compareRepoFrame.visibility = View.VISIBLE
    }

    private fun adaptProfile(order: Int, url: String?) {
        url?.let {
            when (order) {
                1 -> {
                    CustomGlide.drawImage(binding.repo1User1, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                2 -> {
                    CustomGlide.drawImage(binding.repo1User2, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                3 -> {
                    CustomGlide.drawImage(binding.repo1User3, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                4 -> {
                    CustomGlide.drawImage(binding.repo1User4, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                5 -> {
                    CustomGlide.drawImage(binding.repo2User1, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                6 -> {
                    CustomGlide.drawImage(binding.repo2User2, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                7 -> {
                    CustomGlide.drawImage(binding.repo2User3, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }

                8 -> {
                    CustomGlide.drawImage(binding.repo2User4, url) { }
                    binding.compareRepoFrame.visibility = View.VISIBLE
                }
            }
        }
    }

    class ScoreCustomFormatter() : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "${DecimalFormat("#.##").format(value)}%"
        }
    }

}