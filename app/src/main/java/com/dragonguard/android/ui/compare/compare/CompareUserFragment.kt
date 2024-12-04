package com.dragonguard.android.ui.compare.compare

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.data.model.compare.RepoMembersResult
import com.dragonguard.android.data.model.contributors.GitRepoMember
import com.dragonguard.android.databinding.FragmentCompareUserBinding
import com.dragonguard.android.util.CustomGlide
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.launch

//선택한 두 Repository의 member들을 비교하기 위한 fragment
class CompareUserFragment(
    private val repoName1: String,
    private val repoName2: String,
    private val repo1: List<RepoMembersResult>,
    private val repo2: List<RepoMembersResult>
) : Fragment(), UserListAdapter.OnUserClickListener {

    private val contributors1: ArrayList<GitRepoMember> = repo1.map {
        GitRepoMember(
            additions = it.additions,
            commits = it.commits,
            deletions = it.deletions,
            github_id = it.github_id,
            profile_url = it.profile_url,
            is_service_member = it.is_service_member
        )
    } as ArrayList<GitRepoMember>
    private val contributors2: ArrayList<GitRepoMember> = repo2.map {
        GitRepoMember(
            additions = it.additions,
            commits = it.commits,
            deletions = it.deletions,
            github_id = it.github_id,
            profile_url = it.profile_url,
            is_service_member = it.is_service_member
        )
    } as ArrayList<GitRepoMember>
    private var user1 = ""
    private var user2 = ""
    private var count = 0
    private lateinit var binding: FragmentCompareUserBinding
    lateinit var userGroup1: UserSheetfragment
    lateinit var userGroup2: UserSheetfragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compare_user, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        binding.user1Frame.setOnClickListener {
            userGroup1 =
                UserSheetfragment(
                    this,
                    contributors1,
                    contributors2,
                    1,
                    repoName1,
                    repoName2,
                    binding
                )
            userGroup1.show(parentFragmentManager, userGroup1.tag)
        }

        binding.user2Frame.setOnClickListener {
            userGroup2 =
                UserSheetfragment(
                    this,
                    contributors1,
                    contributors2,
                    2,
                    repoName1,
                    repoName2,
                    binding
                )
            userGroup2.show(parentFragmentManager, userGroup2.tag)
        }
        binding.user1Profile.clipToOutline = true
        binding.user2Profile.clipToOutline = true


    }

    //activity 구성 이후 화면을 초기화하는 함수
    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

            }
        }
    }


    /*
    spinner에 선택된 user들을 비교하는 그래프를 그리는 함수
     */
    fun initGraph() {
        binding.userCompareLottie.visibility = View.VISIBLE
//        Toast.makeText(requireContext(), "initGraph()", Toast.LENGTH_SHORT).show()
        var user1Cont = contributors1.find { it.github_id == user1 }
        var user2Cont = contributors2.find { it.github_id == user2 }
        if (user1Cont == null) {
            user1Cont = contributors2.find { it.github_id == user1 }
        }
        if (user2Cont == null) {
            user2Cont = contributors1.find { it.github_id == user2 }
        }
        user1Cont!!
        user2Cont!!
        val commitEntries1 = ArrayList<BarEntry>()
        val commitEntries2 = ArrayList<BarEntry>()
        val codeEntries1 = ArrayList<BarEntry>()
        val codeEntries2 = ArrayList<BarEntry>()
        //Toast.makeText(requireContext(), "${user2Cont.commits} ${user2Cont.additions}  ${user2Cont.deletions}", Toast.LENGTH_SHORT).show()
        commitEntries1.add(BarEntry(1.toFloat(), user1Cont.commits!!.toFloat()))
        codeEntries1.add(BarEntry(1.toFloat(), user1Cont.additions!!.toFloat()))
        codeEntries1.add(BarEntry(2.toFloat(), user1Cont.deletions!!.toFloat()))
        commitEntries1.add(BarEntry(2.toFloat(), user2Cont.commits!!.toFloat()))
        codeEntries2.add(BarEntry(1.toFloat(), user2Cont.additions!!.toFloat()))
        codeEntries2.add(BarEntry(2.toFloat(), user2Cont.deletions!!.toFloat()))
        //Toast.makeText(requireContext(), "entries1 : $entries1", Toast.LENGTH_SHORT).show()

        val commitSet1 = BarDataSet(commitEntries1, user1)
        commitSet1.colors = listOf(Color.rgb(176, 225, 255), Color.rgb(0, 0, 128))
        commitSet1.apply {
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = ScoreCustomFormatter(listOf(user1, user2))
        }


        val commitDataSet1 = ArrayList<IBarDataSet>()
        commitDataSet1.add(commitSet1)
//        commitDataSet1.add(commitSet2)

        val commitData = BarData(commitDataSet1)
        commitData.barWidth = 0.4f

        val codeSet1 = BarDataSet(codeEntries1, user1)
        codeSet1.color = Color.rgb(176, 225, 255)
        codeSet1.apply {
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = CodeFormatter()
        }

        val codeSet2 = BarDataSet(codeEntries2, user2)
        codeSet2.color = Color.rgb(0, 0, 128)
        codeSet2.apply {
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = CodeFormatter()
        }
        val codeDataSet1 = ArrayList<IBarDataSet>()
        codeDataSet1.add(codeSet1)
        codeDataSet1.add(codeSet2)

        val codeData = BarData(codeDataSet1)
        codeData.barWidth = 0.4f
        codeData.groupBars(0.5f, 0.2f, 0f)

        binding.userCommitChart.apply {
            setFitBars(true)
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
                //setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
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
                    ScoreCustomFormatter(listOf(user1, user2)) // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            animateY(500) // 밑에서부터 올라오는 애니매이션 적용
        }

        binding.userCodeChart.apply {
            setFitBars(true)
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
                valueFormatter = CodeFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            animateY(500) // 밑에서부터 올라오는 애니매이션 적용
        }
//        binding.userChart.invalidate()
        binding.userCommitChart.data = commitData
        binding.userCodeChart.data = codeData
//        binding.userChart.data.addDataSet(set2)
//        binding.userChart.invalidate()
        binding.userCommitChart.visibility = View.VISIBLE
        binding.userCodeChart.visibility = View.VISIBLE


//        binding.contributorsChart.run {
//            this.data = data //차트의 데이터를 data로 설정해줌.
//            setFitBars(true)
//
//            invalidate()
//        }
        count = 0
        binding.userCompareLottie.pauseAnimation()
        binding.userCompareLottie.visibility = View.GONE
    }

    /*    그래프 x축을 contributor의 이름으로 변경하는 코드
          x축 label을 githubId의 앞의 4글자를 기입하여 곂치는 문제 해결
     */

    class CodeFormatter() : ValueFormatter() {
        private val days = listOf("additions", "deletions")

        //        private val days = listOf( "additions", "deletions")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString()
        }

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }

    //    막대 위의 커밋수 정수로 변경
    class ScoreCustomFormatter(private val users: List<String>) : ValueFormatter() {
        //        private val days = listOf( "additions", "deletions")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return users.getOrNull(value.toInt() - 1) ?: value.toString().substring(0, 2)
        }

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }

    override fun onFirstUserClick(data: GitRepoMember) {
        CustomGlide.drawImage(
            binding.user1Profile,
            data.profile_url
        ) {}

        binding.user1GithubId.text = data.github_id
        data.github_id?.let {
            user1 = it
        }
        if (user1 != "null" && user2 != "null" && user1.isNotBlank() && user2.isNotBlank()) {
            initGraph()
        }
        userGroup1.dismiss()
    }

    override fun onSecondUserClick(data: GitRepoMember) {
        CustomGlide.drawImage(
            binding.user2Profile,
            data.profile_url
        ) { }
        binding.user2GithubId.text = data.github_id
        user2 = data.github_id.toString()
        if (user1 != "null" && user2 != "null" && user1.isNotBlank() && user2.isNotBlank()) {
            initGraph()
        }
        userGroup2.dismiss()
    }

}