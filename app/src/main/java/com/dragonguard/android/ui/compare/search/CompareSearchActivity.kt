package com.dragonguard.android.ui.compare.search

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.search.RepoSearchResultModel
import com.dragonguard.android.databinding.ActivitySearchBinding
import com.dragonguard.android.util.LoadState
import kotlinx.coroutines.launch

class CompareSearchActivity : AppCompatActivity() {
    private lateinit var compareRepositoryAdapter: SearchCompareRepoAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: CompareSearchViewModel
    private var position = 0
    private var repoNames = ArrayList<RepoSearchResultModel>()
    private var count = 0
    private var changed = true
    private var lastSearch = ""
    private var repoCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        viewModel = CompareSearchViewModel()
        initObserver()

        repoCount = intent.getIntExtra("count", 0)
        if (repoCount == 0) {
            supportActionBar?.setTitle("첫번째 Repository")
        } else {
            supportActionBar?.setTitle("두번째 Repository")
        }
        //Toast.makeText(this, "$repoCount", Toast.LENGTH_SHORT).show()

        /*viewmodel.onOptionListener.observe(this, Observer {

            화면전환 테스트
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)

        })*/


        //edittext에 엔터를 눌렀을때 검색되게 하는 리스너
        binding.searchName.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                if (!binding.searchName.text.isNullOrEmpty()) {
                    Log.d("enter click", "edittext 클릭함")
                    val search = binding.searchName.text!!.toString()
                    binding.searchName.setSelection(search.length)
                    if (search.isNotEmpty()) {
                        if (lastSearch != search) {
                            lastSearch = search
                            repoNames.clear()
                            binding.searchResult.visibility = View.GONE
                            count = 0
                            position = 0
                        }
                        changed = true
                        Log.d("api 시도", "callSearchApi 실행")
                        callSearchApi(lastSearch)
                        binding.searchResult.visibility = View.VISIBLE
                        binding.searchName.isFocusable = true
                    } else {
                        binding.searchName.setText("")
                        Toast.makeText(
                            applicationContext,
                            "엔터 검색어를 입력하세요!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }
            true
        }


    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.LOADING) {
                        binding.loadingLottie.visibility = View.VISIBLE
                    } else if (state.loadState == LoadState.SUCCESS) {
                        binding.loadingLottie.visibility = View.GONE
                        repoNames.addAll(state.searchResults.searchResults)
                        initRecycler()
                    } else if (state.loadState == LoadState.ERROR) {
                        binding.loadingLottie.visibility = View.GONE
                    }
                }
            }
        }
    }

    //    repo 검색 api 호출 및 결과 출력
    private fun callSearchApi(name: String) {
        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.searchRepo(name, count)
    }

    //    받아온 데이터를 리사이클러뷰에 추가하는 함수 initRecycler()
    private fun initRecycler() {
        viewModel.addReceivedRepo()
        Log.d("count", "count: $count")
        if (count == 0) {
            compareRepositoryAdapter = SearchCompareRepoAdapter(
                repoNames,
                this,
                repoCount,
                viewModel.currentState.token.token
            )
            binding.searchResult.adapter = compareRepositoryAdapter
            binding.searchResult.layoutManager = LinearLayoutManager(this)
            compareRepositoryAdapter.notifyDataSetChanged()
            binding.searchResult.visibility = View.VISIBLE
        }
        count++
        binding.searchResult.adapter?.notifyDataSetChanged()
        Log.d("api 횟수", "$count 페이지 검색")
        binding.loadingLottie.visibility = View.GONE
        initScrollListener()
    }


    //    데이터 더 받아오는 함수 loadMorePosts() 구현
    private fun loadMorePosts() {
        if (binding.loadingLottie.visibility == View.GONE && count != 0) {
            binding.loadingLottie.visibility = View.VISIBLE
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

    //    edittext의 키보드 제거
    private fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchName.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }


}