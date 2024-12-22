package com.dragonguard.android.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivitySearchBinding
import com.dragonguard.android.ui.profile.other.OthersProfileActivity
import com.dragonguard.android.ui.search.filter.SearchFilterActivity
import com.dragonguard.android.ui.search.repo.RepoContributorsActivity
import com.dragonguard.android.util.HorizontalItemDecorator
import com.dragonguard.android.util.LoadState
import com.dragonguard.android.util.VerticalItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
 repo를 이름으로 검색하는 activity
 */
@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), RepositoryProfileAdapter.OnRepositoryClickListener {
    private lateinit var binding: ActivitySearchBinding
    lateinit var repositoryProfileAdapter: RepositoryProfileAdapter
    private var position = 0
    private var count = 0
    private var changed = true
    private var lastSearch = ""
    private var popularLanguages = ArrayList<String>()
    private lateinit var languagesCheckBox: ArrayList<Boolean>
    private val viewModel by viewModels<SearchViewModel>()
    private var filterLanguage = StringBuilder()
    private var filterOptions = StringBuilder()
    private var filterResult = StringBuilder()
    private var type = "GIT_REPO"
    private var token = ""
    private val imgList = HashMap<String, Int>()
    private var repoCount = 0
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 0) {
                val filterIntent = it.data
                try {
                    val language = filterIntent?.getStringExtra("language")
                    val type = filterIntent?.getStringExtra("type")
                    val topics = filterIntent?.getStringExtra("topics")
                    val stars = filterIntent?.getStringExtra("stars")
                    val forks = filterIntent?.getStringExtra("forks")
                    val name = filterIntent?.getStringExtra("name")
                    Log.d("results", "type: $type ")
                    Log.d("results", "language: $language ")
                    Log.d("results", "topics: $topics ")
                    Log.d("results", "stars: $stars ")
                    Log.d("results", "forks: $forks ")
                    Log.d("results", "name: $name ")
                    if (name != lastSearch) {
                        viewModel.clearRepoNames()
                        viewModel.clearUserNames()
                        binding.searchResult.visibility = View.GONE
                    }
                    checkLanguage(
                        languages = language,
                        type = type,
                        topics = topics,
                        stars = stars,
                        forks = forks,
                        name = name
                    )
                } catch (e: Exception) {

                }

            } else if (it.resultCode == 1) {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        this.onBackPressedDispatcher.addCallback(this, callback)
        binding.searchName.isFocusable = false
        initObserver()
        binding.searchResult.addItemDecoration(VerticalItemDecorator(20))
        binding.searchResult.addItemDecoration(HorizontalItemDecorator(10))

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        val intent = intent
        repoCount = intent.getIntExtra("count", 0)
        popularLanguages = arrayListOf(
            "C",
            "C#",
            "C++",
            "CoffeeScript ",
            "CSS",
            "Dart",
            "DM",
            "Elixir",
            "Go",
            "Groovy",
            "HTML",
            "Java",
            "JavaScript",
            "Kotlin",
            "Objective-C",
            "Perl",
            "PHP",
            "PowerShell",
            "Python",
            "Ruby",
            "Rust",
            "Scala",
            "Shell",
            "Swift",
            "TypeScript"
        )
        imgList.apply {
            put("C", R.drawable.c)
            put("C#", R.drawable.c_sharp)
            put("C++", R.drawable.c_puls_plus)
            put("CoffeeScript", R.drawable.coffeescript)
            put("CSS", R.drawable.css)
            put("Dart", R.drawable.dart)
            put("Elixir", R.drawable.elixir)
            put("Go", R.drawable.go)
            put("Groovy", R.drawable.groovy)
            put("HTML", R.drawable.html_5)
            put("Java", R.drawable.java)
            put("JavaScript", R.drawable.javascript)
            put("Kotlin", R.drawable.kotlin)
            put("Objective-C", R.drawable.objective_c)
            put("Perl", R.drawable.perl)
            put("PHP", R.drawable.php)
            put("PowerShell", R.drawable.powershell)
            put("Python", R.drawable.python)
            put("Ruby", R.drawable.ruby)
            put("Rust", R.drawable.rust)
            put("Scala", R.drawable.scala)
            put("Shell", R.drawable.shell)
            put("Swift", R.drawable.swift)
            put("TypeScript", R.drawable.typescript)
        }
        languagesCheckBox = ArrayList<Boolean>()
        for (i in 0 until popularLanguages.size) {
            languagesCheckBox.add(false)
        }
//        Toast.makeText(applicationContext, "크기 : ${popularLanguages.size}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(applicationContext, "checkbox : ${languagesCheckBox.size}", Toast.LENGTH_SHORT).show()
//        검색 옵션 구현

        binding.searchName.setOnClickListener {
            activityResultLauncher.launch(Intent(this, SearchFilterActivity::class.java))
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.searchState == LoadState.REPO_SUCCESS) {
                        checkRepoNames()
                    } else if (it.searchState == LoadState.USER_SUCCESS) {
                        checkUserNames()
                    }
                }
            }
        }
    }

    private fun checkUserNames() {
        if (viewModel.currentState.receivedUserNames.userNames.isNotEmpty()) {
            viewModel.addReceivedUserNames()
            initRecycler()
        } else {
            binding.loadingLottie.pauseAnimation()
            binding.loadingLottie.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
        }
    }

    private fun checkRepoNames() {
        if (viewModel.currentState.receivedRepoNames.repoNames.isNotEmpty()) {
            viewModel.addReceivedRepoNames()
            initRecycler()
        } else {
            binding.loadingLottie.pauseAnimation()
            binding.loadingLottie.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
        }
    }


    private fun checkLanguage(
        languages: String?,
        type: String?,
        topics: String?,
        stars: String?,
        forks: String?,
        name: String?
    ) {
        count = 0
        binding.searchOption.removeAllViews()
        filterResult = StringBuilder()
        filterLanguage = StringBuilder()
        filterOptions = StringBuilder()
        if (type != null) {
            this.type = type
        }
        if (languages != null) {
            val splitLang = languages.split(",").toMutableList()
            val joinToString: String = splitLang.map {
                "language:$it"
            }.joinToString(",")
            Log.d("test", "filter language: $joinToString")
            filterLanguage.append(joinToString)
        }

        if (topics != null) {
            when (topics) {
                "0개" -> {
                    filterOptions.append("topics:0,")
                }

                "1개" -> {
                    filterOptions.append("topics:1,")
                }

                "2개" -> {
                    filterOptions.append("topics:2,")
                }

                "3개" -> {
                    filterOptions.append("topics:3,")
                }

                "4개 이상" -> {
                    filterOptions.append("topics:>=4,")
                }
            }
        }
        if (stars != null) {
            when (stars) {
                "10개 미만" -> {
                    filterOptions.append("stars:0..9,")
                }

                "50개 미만" -> {
                    filterOptions.append("stars:10..49,")
                }

                "100개 미만" -> {
                    filterOptions.append("stars:50..99,")
                }

                "500개 미만" -> {
                    filterOptions.append("stars:100..499,")
                }

                "500개 이상" -> {
                    filterOptions.append("stars:>500,")
                }
            }

        }
        if (forks != null) {
            when (forks) {
                "10개 미만" -> {
                    filterOptions.append("forks:0..9")
                }

                "50개 미만" -> {
                    filterOptions.append("forks:10..49")
                }

                "100개 미만" -> {
                    filterOptions.append("forks:50..99")
                }

                "500개 미만" -> {
                    filterOptions.append("forks:100..499")
                }

                "500개 이상" -> {
                    filterOptions.append("forks:>500")
                }
            }
        }
        val splitOption = filterOptions.split(",").toMutableList()
        val joinToString: String = splitOption.filter {
            it.isNotBlank()
        }.joinToString(",")
        Log.d("test", "filter option: $joinToString")
        filterOptions = StringBuilder(joinToString)
//        count = 0
//        Toast.makeText(applicationContext, "filters:$filterOptions", Toast.LENGTH_SHORT).show()
//        Toast.makeText(applicationContext, "filters:$filterLanguage", Toast.LENGTH_SHORT).show()
        filterCat(name!!)
    }

    private fun filterCat(name: String) {
        lastSearch = name
        Log.d("횟수", "filtercat 나옴!!!!!!")
        if (filterLanguage.isNotEmpty()) {
            if (filterOptions.isNotEmpty()) {
                filterResult.append(filterLanguage)
                filterResult.append(",")
                filterResult.append(filterOptions)
            } else {
                filterResult.append(filterLanguage)
            }
        } else {
            if (filterOptions.isNotEmpty()) {
                filterResult.append(filterOptions)
            } else {
                filterResult = StringBuilder()
            }
        }
        Log.d("횟수", "filtercat $filterResult")
        callSearchApi(name)


        binding.searchOption.visibility = View.VISIBLE
//        binding.chosenFilters.text = chosenFilters.toString()
    }


    //    repo 검색 api 호출 및 결과 출력
    // API 완성시 주석 제거
    private fun callSearchApi(name: String) {
        binding.loadingLottie.visibility = View.VISIBLE
        binding.loadingLottie.playAnimation()
        Log.d("필터", "total filters: ${filterResult.toString()}")
        if (!this@SearchActivity.isFinishing) {
            if (type.isNotBlank()) {
                if (type == "MEMBER") {
                    viewModel.searchUserNames(name, count, type)
                    count++
                } else {
                    if (filterResult.toString().isBlank()) {
                        Log.d("필터 없음", "필터 없음")
                        viewModel.searchRepositoryNamesNoFilters(name, count, type)
                    } else {
                        Log.d("필터 있음", "필터 있음")
                        viewModel.searchRepositoryNamesWithFilters(
                            name,
                            count,
                            filterResult.toString(),
                            type
                        )
                    }
                }
            } else {
                Log.d("필터 없음", "필터 없음")
                viewModel.searchRepositoryNamesNoFilters(name, count, "GIT_REPO")
            }
        }
    }


    //    받아온 데이터를 리사이클러뷰에 추가하는 함수 initRecycler()
    private fun initRecycler() {
        Log.d("count", "count: $count")
        Log.d("results", viewModel.currentState.userNames.userNames.toString())
        Log.d("results", viewModel.currentState.repoNames.repoNames.toString())
        if (type == "MEMBER") {
            if (count == 0) {
                repositoryProfileAdapter =
                    RepositoryProfileAdapter(
                        viewModel.currentState.userNames.userNames,
                        imgList,
                        repoCount,
                        this@SearchActivity
                    )
                binding.searchResult.adapter = repositoryProfileAdapter
                binding.searchResult.layoutManager = LinearLayoutManager(this)
                binding.searchResult.visibility = View.VISIBLE
            }
            repositoryProfileAdapter.notifyDataSetChanged()
        } else {
            if (count == 0) {
                repositoryProfileAdapter = if (type.isBlank()) {
                    RepositoryProfileAdapter(
                        viewModel.currentState.repoNames.repoNames,
                        imgList,
                        repoCount,
                        this@SearchActivity
                    )
                } else {
                    RepositoryProfileAdapter(
                        viewModel.currentState.repoNames.repoNames,
                        imgList,
                        repoCount,
                        this@SearchActivity
                    )
                }
                binding.searchResult.adapter = repositoryProfileAdapter
                binding.searchResult.layoutManager = LinearLayoutManager(this)
                repositoryProfileAdapter.notifyDataSetChanged()
                binding.searchResult.visibility = View.VISIBLE
            } else {
                repositoryProfileAdapter.notifyDataSetChanged()
            }
        }

        count++
        Log.d("api 횟수", "$count 페이지 검색")
        binding.loadingLottie.pauseAnimation()
        binding.loadingLottie.visibility = View.GONE
        initScrollListener()
        type = "GIT_REPO"
    }


    //    데이터 더 받아오는 함수 loadMorePosts() 구현
    private fun loadMorePosts() {
        if (binding.loadingLottie.visibility == View.GONE && (viewModel.currentState.repoNames.repoNames.size >= count * 10 || viewModel.currentState.userNames.userNames.size >= count * 10)) {
            val params = binding.loadingLottie.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            params.bottomMargin = 100
            binding.loadingLottie.layoutParams = params
            binding.loadingLottie.visibility = View.VISIBLE
            binding.loadingLottie.playAnimation()
            changed = true
            callSearchApi(lastSearch)
        }
    }

    //마지막 item에서 스크롤 하면 로딩과 함께 다시 받아서 추가하기
    private fun initScrollListener() {
        binding.searchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.searchResult.layoutManager
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                position = recyclerView.adapter!!.itemCount - 1
                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                if (!binding.searchResult.canScrollVertically(1) && lastVisibleItem == itemTotalCount) {
                    loadMorePosts()
                }
            }
        })
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.d("search", "search back pressed")
            finish()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d("search", "search back pressed")
                finish()
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSearchRepositoryClick(repoName: String) {
        Intent(this, RepoContributorsActivity::class.java).apply {
            putExtra("repoName", repoName)
        }.run { startActivity(this) }
    }

    override fun onCompareSearchResultRepositoryClick(repoName: String) {
        val intent = Intent()
        intent.putExtra("repoName", repoName)
        setResult(repoCount, intent)
        finish()
    }

    override fun onUserNameSearchClick(userName: String) {
        Intent(this, OthersProfileActivity::class.java).apply {
            putExtra("userName", userName)
        }.run { startActivity(this) }
    }

    override fun onUserNotServiceMemberClick(userName: String) {
        Toast.makeText(
            this,
            "$userName 은(는) 회원이 아닙니다.",
            Toast.LENGTH_SHORT
        ).show()
    }
}