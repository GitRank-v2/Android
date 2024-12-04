package com.dragonguard.android.ui.menu.org.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.R
import com.dragonguard.android.data.model.org.OrganizationNamesModel
import com.dragonguard.android.databinding.ActivitySearchOrganizationBinding
import com.dragonguard.android.ui.menu.org.regist.RegistOrgActivity
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchOrganizationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchOrganizationBinding
    private val searchOrgAdapter by lazy { SearchOrganizationAdapter(this) }
    private val viewModel by viewModels<SearchOrganizationViewModel>()
    private var position = 0
    private var count = 0
    private var type = ""
    private var lastSearch = ""
    private var changable = true
    private var typeChanged = false
    private var orgNames = OrganizationNamesModel()
    private var chosenType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_organization)
        initObserver()
        chosenType = intent?.getStringExtra("type")
        if (chosenType != null) {
            when (chosenType) {
                "대학교" -> {
                    binding.orgGroup.check(R.id.org_type0)
                    type = "UNIVERSITY"
                }

                "고등학교" -> {
                    binding.orgGroup.check(R.id.org_type1)
                    type = "HIGH_SCHOOL"
                }

                "회사" -> {
                    binding.orgGroup.check(R.id.org_type2)
                    type = "COMPANY"
                }

                "etc" -> {
                    binding.orgGroup.check(R.id.org_type3)
                    type = "ETC"
                }

            }
            binding.orgGroup.isEnabled = false
        }

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.title = "   조직 검색 및 인증"

        binding.orgGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.org_type0 -> {
                    type = "UNIVERSITY"
                }

                R.id.org_type0 -> {
                    type = "HIGH_SCHOOL"
                }

                R.id.org_type2 -> {
                    type = "COMPANY"
                }

                R.id.org_type3 -> {
                    type = "ETC"
                }

            }
            typeChanged = true
        }
        //        검색 아이콘 눌렀을때 검색 구현
        binding.searchIcon.setOnClickListener {
            if (!binding.searchName.text.isNullOrEmpty()) {
                if (lastSearch != binding.searchName.text.toString() || typeChanged) {
                    orgNames.data = emptyList()
                    binding.searchResult.visibility = View.GONE
                    count = 0
                    position = 0
                    typeChanged = false
                }
                changable = true
                lastSearch = binding.searchName.text.toString()
                getOrganizationNames(binding.searchName.text.toString())
                binding.searchResult.visibility = View.VISIBLE
                binding.searchName.isFocusable = true
            } else {
                Toast.makeText(applicationContext, "검색어를 입력하세요!!", Toast.LENGTH_SHORT).show()
                closeKeyboard()
            }
        }

        //        edittext에 엔터를 눌렀을때 검색되게 하는 리스너
        binding.searchName.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (!binding.searchName.text.isNullOrEmpty()) {
                    Log.d("enter click", "edittext 클릭함")
                    val search = binding.searchName.text!!
                    binding.searchName.setText(search)
                    binding.searchName.setSelection(binding.searchName.length())
                    if (search.isNotEmpty()) {
                        closeKeyboard()
                        if (lastSearch != binding.searchName.text.toString() || typeChanged) {
                            orgNames.data = emptyList()
                            binding.searchResult.visibility = View.GONE
                            count = 0
                            position = 0
                            typeChanged = false
                        }
                        changable = true
                        lastSearch = binding.searchName.text.toString()
                        getOrganizationNames(binding.searchName.text.toString())
                        binding.searchResult.visibility = View.VISIBLE
                        binding.searchName.isFocusable = true
                    } else {
                        binding.searchName.setText("")
                        Toast.makeText(
                            applicationContext,
                            "검색어를 입력하세요!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        closeKeyboard()
                    }

                }
            }
            true
        }

        binding.noneOrganization.setOnClickListener {
            val intent = Intent(applicationContext, RegistOrgActivity::class.java)
            //intent.putExtra("token", token)
            startActivity(intent)
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.state == LoadState.SUCCESS) {
                        if (checkSearchResult(state.orgNames.names)) {
                            initRecycler()
                        }
                    }
                }
            }
        }
    }

    private fun getOrganizationNames(name: String) {
        Log.d("org 검색", "이름 $name type $type  count $count")
        if (type.isBlank()) {
            type = "UNIVERSITY"
            viewModel.searchOrgNames(name, type, count)
        }
    }

    private fun checkSearchResult(searchResult: OrganizationNamesModel): Boolean {
        return when (searchResult.data.isNullOrEmpty()) {
            true -> {
                if (changable) {
                    changable = false
                    false
                } else {
                    true
                }
            }

            false -> {
                changable = false
                Log.d("api 시도", "api 성공$searchResult")
                for (i in 0 until searchResult.data.size) {
                    val compare = orgNames.data.filter { it.name == searchResult.data[i].name }
                    if (compare.isEmpty()) {
                        orgNames.data += searchResult.data[i]
                    }
                }
                true
            }
        }

    }

    private fun initRecycler() {
        viewModel.addReceivedOrgNames()
        Log.d("count", "count: $count")
        if (count == 0) {
            binding.orgListTitle.text = "$type 목록"
            binding.searchResult.adapter = searchOrgAdapter
            binding.searchResult.layoutManager = LinearLayoutManager(this)

            binding.searchResult.visibility = View.VISIBLE
        }
        searchOrgAdapter.submitList(orgNames.data)
        count++
        Log.d("api 횟수", "$count 페이지 검색")
        binding.progressBar.visibility = View.GONE
        initScrollListener()
    }

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

    private fun loadMorePosts() {
        if (binding.progressBar.visibility == View.GONE && count != 0) {
            binding.progressBar.visibility = View.VISIBLE
            changable = true
            getOrganizationNames(binding.searchName.text.toString())
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    //    edittext의 키보드 제거
    private fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchName.windowToken, 0)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.home, binding.toolbar.menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
//            R.id.home_menu -> {
//                val intent = Intent(applicationContext, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//            }
        }
        return super.onOptionsItemSelected(item)
    }
}