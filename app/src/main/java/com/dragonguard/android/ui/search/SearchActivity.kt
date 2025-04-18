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
    private lateinit var repositoryProfileAdapter: RepositoryProfileAdapter
    private var position = 0
    private var count = 1
    private var changed = true
    private var lastSearch = ""
    private lateinit var languagesCheckBox: ArrayList<Boolean>
    private val viewModel by viewModels<SearchViewModel>()
    private var filterLanguage = StringBuilder()
    private var filterOptions = StringBuilder()
    private var filterResult = StringBuilder()
    private var type = getString(R.string.repo)
    private val imgList = HashMap<String, Int>()
    private var repoCount = 0
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 0) {
                val filterIntent = it.data
                try {
                    val type = filterIntent?.getStringExtra(getString(R.string.type))
                    val name =
                        filterIntent?.getStringExtra(getString(R.string.name))
                            ?: return@registerForActivityResult
                    if (name != lastSearch) {
                        viewModel.clearRepoNames()
                        viewModel.clearUserNames()
                        binding.searchResult.visibility = View.GONE
                    }
                    if (type == getString(R.string.member)) {
                        if (type != this.type) {
                            viewModel.clearUserNames()
                            viewModel.clearRepoNames()
                            binding.searchResult.visibility = View.GONE
                        }
                        callSearchApi(name)
                    } else {
                        val language = filterIntent.getStringExtra(getString(R.string.language))
                        val topics = filterIntent.getStringExtra(getString(R.string.topics))
                        val stars = filterIntent.getStringExtra(getString(R.string.star))
                        val forks = filterIntent.getStringExtra(getString(R.string.fork))
                        checkFilters(
                            languages = language,
                            type = type,
                            topics = topics,
                            stars = stars,
                            forks = forks,
                            name = name
                        )
                    }

                } catch (e: Exception) {

                }

            } else if (it.resultCode == 1) {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        if (viewModel.currentState.userNames.userNames.isNotEmpty()) {
            initRecycler()
        } else {
            binding.loadingLottie.pauseAnimation()
            binding.loadingLottie.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
        }
    }

    private fun checkRepoNames() {
        if (viewModel.currentState.repoNames.repoNames.isNotEmpty()) {
            initRecycler()
        } else {
            binding.loadingLottie.pauseAnimation()
            binding.loadingLottie.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
        }
    }


    private fun checkFilters(
        languages: String?,
        type: String?,
        topics: String?,
        stars: String?,
        forks: String?,
        name: String?
    ) {
        count = 1
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
                ZERO_TOPIC -> {
                    filterOptions.append(ZERO_TOPIC_FILTER)
                }

                ONE_TOPIC -> {
                    filterOptions.append(ONE_TOPIC_FILTER)
                }

                TWO_TOPIC -> {
                    filterOptions.append(TWO_TOPIC_FILTER)
                }

                THREE_TOPIC -> {
                    filterOptions.append(THREE_TOPIC_FILTER)
                }

                FOUR_OR_MORE_TOPIC -> {
                    filterOptions.append(FOUR_OR_MORE_TOPIC_FILTER)
                }
            }
        }
        if (stars != null) {
            when (stars) {
                LESS_THAN_10 -> {
                    filterOptions.append(LESS_THAN_10_STARS_FILTER)
                }

                LESS_THAN_50 -> {
                    filterOptions.append(LESS_THAN_50_STARS_FILTER)
                }

                LESS_THAN_100 -> {
                    filterOptions.append(LESS_THAN_100_STARS_FILTER)
                }

                LESS_THAN_500 -> {
                    filterOptions.append(LESS_THAN_500_STARS_FILTER)
                }

                MORE_THAN_500 -> {
                    filterOptions.append(MORE_THAN_500_STARS_FILTER)
                }
            }

        }
        if (forks != null) {
            when (forks) {
                LESS_THAN_10 -> {
                    filterOptions.append(LESS_THAN_10_FORKS_FILTER)
                }

                LESS_THAN_50 -> {
                    filterOptions.append(LESS_THAN_50_FORKS_FILTER)
                }

                LESS_THAN_100 -> {
                    filterOptions.append(LESS_THAN_100_FORKS_FILTER)
                }

                LESS_THAN_500 -> {
                    filterOptions.append(LESS_THAN_500_FORKS_FILTER)
                }

                MORE_THAN_500 -> {
                    filterOptions.append(MORE_THAN_500_FORKS_FILTER)
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
    private fun callSearchApi(name: String) {
        binding.loadingLottie.visibility = View.VISIBLE
        binding.loadingLottie.playAnimation()
        Log.d("필터", "total filters: ${filterResult.toString()}")
        if (!this@SearchActivity.isFinishing) {
            if (type.isNotBlank()) {
                if (type == getString(R.string.member)) {
                    viewModel.searchUserNames(name, count)
                } else {
                    if (filterResult.toString().isBlank()) {
                        Log.d("필터 없음", "필터 없음")
                        viewModel.searchRepositoryNamesNoFilters(name, count)
                    } else {
                        Log.d("필터 있음", "필터 있음")
                        viewModel.searchRepositoryNamesWithFilters(
                            name,
                            count,
                            filterResult.toString()
                        )
                    }
                }
            } else {
                Log.d("필터 없음", "필터 없음")
                viewModel.searchRepositoryNamesNoFilters(name, count)
            }
        }
    }


    //    받아온 데이터를 리사이클러뷰에 추가하는 함수 initRecycler()
    private fun initRecycler() {
        Log.d("count", "count: $count")
        Log.d("names", viewModel.currentState.userNames.userNames.toString())
        Log.d("repos", viewModel.currentState.repoNames.repoNames.toString())
        if (type == getString(R.string.member)) {
            if (count == 1) {
                repositoryProfileAdapter = RepositoryProfileAdapter(
                    viewModel.currentState.userNames.userNames,
                    imgList,
                    repoCount,
                    this@SearchActivity
                )
                binding.searchResult.adapter = repositoryProfileAdapter
                binding.searchResult.layoutManager = LinearLayoutManager(this)
            }
            binding.searchResult.visibility = View.VISIBLE
            repositoryProfileAdapter.notifyDataSetChanged()
            Log.d(getString(R.string.member), viewModel.currentState.userNames.userNames.toString())
        } else {
            if (count == 1) {
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

            }
            Log.d("repository", viewModel.currentState.repoNames.repoNames.toString())
            repositoryProfileAdapter.notifyDataSetChanged()
            binding.searchResult.visibility = View.VISIBLE
        }
        Log.d("api 횟수", "$count 페이지 검색")
        count++

        binding.loadingLottie.pauseAnimation()
        binding.loadingLottie.visibility = View.GONE
        initScrollListener()
    }


    //    데이터 더 받아오는 함수 loadMorePosts() 구현
    private fun loadMorePosts() {
        //Log.d("더 가져오기", viewModel.currentState.repoNames.repoNames.size.toString())
        //Log.d("더 가져오기", viewModel.currentState.userNames.userNames.size.toString())
        //Log.d("더 가져오기", count.toString())
        if (binding.loadingLottie.visibility == View.GONE &&
            (viewModel.currentState.repoNames.repoNames.size >= (count - 1) * 10
                    || viewModel.currentState.userNames.userNames.size >= (count - 1) * 10)
        ) {
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

    companion object {
        private val popularLanguages = arrayListOf(
            "C",
            "C#",
            "C++",
            "CoffeeScript",
            "CSS",
            "Dart",
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

        private const val ZERO_TOPIC = "0개"
        private const val ONE_TOPIC = "1개"
        private const val TWO_TOPIC = "2개"
        private const val THREE_TOPIC = "3개"
        private const val FOUR_OR_MORE_TOPIC = "4개 이상"
        private const val LESS_THAN_10 = "10개 미만"
        private const val LESS_THAN_50 = "50개 미만"
        private const val LESS_THAN_100 = "100개 미만"
        private const val LESS_THAN_500 = "500개 미만"
        private const val MORE_THAN_500 = "500개 이상"

        private const val ZERO_TOPIC_FILTER = "topics:0,"
        private const val ONE_TOPIC_FILTER = "topics:1,"
        private const val TWO_TOPIC_FILTER = "topics:2,"
        private const val THREE_TOPIC_FILTER = "topics:3,"
        private const val FOUR_OR_MORE_TOPIC_FILTER = "topics:>=4,"

        private const val LESS_THAN_10_STARS_FILTER = "stars:0..9,"
        private const val LESS_THAN_50_STARS_FILTER = "stars:10..49,"
        private const val LESS_THAN_100_STARS_FILTER = "stars:50..99,"
        private const val LESS_THAN_500_STARS_FILTER = "stars:100..499,"
        private const val MORE_THAN_500_STARS_FILTER = "stars:>500,"

        private const val LESS_THAN_10_FORKS_FILTER = "forks:0..9"
        private const val LESS_THAN_50_FORKS_FILTER = "forks:10..49"
        private const val LESS_THAN_100_FORKS_FILTER = "forks:50..99"
        private const val LESS_THAN_500_FORKS_FILTER = "forks:100..499"
        private const val MORE_THAN_500_FORKS_FILTER = "forks:>500"
    }
}