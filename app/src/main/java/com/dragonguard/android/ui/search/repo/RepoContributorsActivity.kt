package com.dragonguard.android.ui.search.repo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.R
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.data.model.contributors.RepoContributorsModel
import com.dragonguard.android.databinding.ActivityRepoContributorsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.launch

/*
 선택한 repo의 contributor들과 기여 정도를 보여주고
 막대그래프로 시각화해서 보여주는 activity
 */
class RepoContributorsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepoContributorsBinding
    lateinit var contributorsAdapter: ContributorsAdapter
    private var repoName = ""
    private lateinit var viewModel: RepoContributorsViewModel
    private val colorSets = ArrayList<Int>()
    private var sparkLines = mutableListOf<Int>()
    private var refresh = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = RepoContributorsViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repo_contributors)
        initObserver()

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        repoName = intent.getStringExtra("repoName")!!
//        Toast.makeText(applicationContext, "reponame = $repoName", Toast.LENGTH_SHORT).show()
        repoContributors(repoName)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.loadState is RepoContributorsContract.RepoContributorsState.LoadState.Success) {
                        checkContributors(it.repoState.repoState)
                    }
                }


            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect {
                    when (it) {
                        is RepoContributorsContract.RepoContributorsEffect.ShowToast -> {
                            Toast.makeText(
                                applicationContext,
                                "데이터를 불러오는데 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    //    repo의 contributors 검색
    fun repoContributors(repoName: String) {
        binding.loadingLottie.resumeAnimation()
        binding.loadingLottie.visibility = View.VISIBLE
        if (!this@RepoContributorsActivity.isFinishing) {
            Log.d("check", "repoName $repoName")
            viewModel.getRepoContributors(repoName)
        }
    }

    //    검색한 결과가 잘 왔는지 확인
    fun checkContributors(result: RepoContributorsModel?) {
        Log.d("repo", "결과 $result")
        if (result != null) {
            if (!result.git_repo_members.isNullOrEmpty()) {
                if (result.git_repo_members[0].additions == null) {
                    //재시도
                } else {
                    result.spark_line?.let {
                        sparkLines = it.toMutableList()
                    }
                    initRecycler()
                }
            } else {
                //재시도
            }
        } else {
            binding.loadingLottie.pauseAnimation()
            binding.loadingLottie.visibility = View.GONE
        }
    }


    //    리사이클러뷰 실행
    private fun initRecycler() {
        binding.repoContributors.setItemViewCacheSize(viewModel.currentState.repoState.repoState.git_repo_members!!.size)
//        Toast.makeText(applicationContext, "리사이클러뷰 시작", Toast.LENGTH_SHORT).show()
//        Toast.makeText(applicationContext, "contributors 수 : ${contributors.size}", Toast.LENGTH_SHORT).show()
        contributorsAdapter = ContributorsAdapter(
            viewModel.currentState.repoState.repoState.git_repo_members!! as ArrayList<GitRepoMember>,
            this,
            colorSets,
            viewModel.currentState.token.token,
            repoName
        )
        binding.repoContributors.adapter = contributorsAdapter
        binding.repoContributors.layoutManager = LinearLayoutManager(this)
        binding.repoTitle.text = repoName
        binding.repoContributors.visibility = View.VISIBLE
        binding.loadingLottie.pauseAnimation()
        binding.loadingLottie.visibility = View.GONE
        binding.repoContributeFrame.setBackgroundResource(R.drawable.shadow)
        binding.hiddenText.visibility = View.VISIBLE
        initGraph()
    }

    //    그래프 그리기
    private fun initGraph() {
//        Toast.makeText(applicationContext,"그래프 그리기 시작", Toast.LENGTH_SHORT).show()
        val entries = ArrayList<BarEntry>()
        val sparkEntries = ArrayList<Entry>()
        var countN = 1
        viewModel.currentState.repoState.repoState.git_repo_members!!.forEach {
            it.commits?.let { commit ->
                entries.add(BarEntry(countN.toFloat(), commit.toFloat()))
                countN++
            }
//                Toast.makeText(applicationContext, "현재 count = $count", Toast.LENGTH_SHORT).show()
        }
        sparkLines.forEachIndexed { index, i ->
            sparkEntries.add(Entry((index + 1).toFloat(), i.toFloat()))
        }

        val set = BarDataSet(entries, "DataSet")
        set.colors = colorSets
        set.apply {
            formSize = 15f
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter =
                ScoreCustomFormatter(viewModel.currentState.repoState.repoState.git_repo_members!!)
            setDrawIcons(true)
        }

        val lineDataSet = LineDataSet(sparkEntries, "Line Data Set")
        lineDataSet.apply {
            color = Color.GREEN
            setDrawCircles(false)
            setDrawValues(false)
        }


        val dataSet = ArrayList<IBarDataSet>()
        dataSet.add(set)
        val data = BarData()
        data.addDataSet(set)

        val spartSets = ArrayList<ILineDataSet>()
        spartSets.add(lineDataSet)
        val lineData = LineData(spartSets)

        binding.contributorsChart.apply {
//            Toast.makeText(applicationContext, "그래프 entry  = ${entries.size}", Toast.LENGTH_SHORT).show()
//            setDrawValueAboveBar(true) // 입력?값이 차트 위or아래에 그려질 건지 (true=위, false=아래)
//            setMaxVisibleValueCount(entries.size) // 최대 보이는 그래프 개수를 contributors의 개수로 지정
            setDrawBarShadow(false) // 그래프 그림자
            setTouchEnabled(false) // 차트 터치 막기
            setPinchZoom(false) // 두손가락으로 줌 설정
            setDrawGridBackground(false) // 격자구조
            description.isEnabled = false // 그래프 오른쪽 하단에 라벨 표시
            legend.isEnabled = false // 차트 범례 설정(legend object chart)
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            axisLeft.apply { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMinimum = 0f // 최소값 0
                granularity = 10f // 10 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(true) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.black) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.black) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.black) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.apply {
                yOffset = 0f
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.black) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter =
                    MyXAxisFormatter(viewModel.currentState.repoState.repoState.git_repo_members!!) // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            animateY(500) // 밑에서부터 올라오는 애니매이션 적용
        }

        binding.repoSpark.apply {
            setTouchEnabled(false) // 차트 터치 막기
            setPinchZoom(false) // 두손가락으로 줌 설정
            description.isEnabled = false // 그래프 오른쪽 하단에 라벨 표시
            legend.isEnabled = false // 차트 범례 설정(legend object chart)
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            animateY(1000, Easing.EaseInOutCubic)
            animateX(1000, Easing.EaseInOutCubic)
        }

        data.apply {
            setValueTextSize(12f)
            barWidth = 0.3f //막대 너비 설정
        }
        binding.contributorsChart.data = data
        binding.contributorsChart.invalidate()
        binding.contributorsChart.visibility = View.VISIBLE

        binding.repoSpark.data = lineData
        binding.repoSpark.invalidate()
        binding.repoSpark.visibility = View.VISIBLE
        binding.repoContributeFrame.visibility = View.VISIBLE

//        binding.contributorsChart.run {
//            this.data = data //차트의 데이터를 data로 설정해줌.
//            setFitBars(true)
//
//            invalidate()
//        }
    }


    /*    그래프 x축을 contributor의 이름으로 변경하는 코드
          x축 label을 githubId의 앞의 4글자를 기입하여 곂치는 문제 해결
     */
    class MyXAxisFormatter(contributors: List<GitRepoMember>) :
        ValueFormatter() {
        private val days = contributors.flatMap {
            if (it.github_id!!.length < 4) {
                arrayListOf(it.github_id!!.substring(0, it.github_id!!.length))
            } else arrayListOf(it.github_id!!.substring(0, 3))
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString()
        }

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }

    //    막대 위의 커밋수 정수로 변경
    class ScoreCustomFormatter(contributors: List<GitRepoMember>) :
        ValueFormatter() {
        private val days = contributors.flatMap {
            if (it.github_id!!.length < 4) {
                arrayListOf(it.github_id!!.substring(0, it.github_id!!.length))
            } else arrayListOf(it.github_id!!.substring(0, 3))
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString().substring(0, 2)
        }

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh, binding.toolbar.menu)
        return true
    }

    //    뒤로가기, 홈으로 화면전환 기능
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.refresh_button -> {
                if (refresh) {
                    //updateContributions()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}