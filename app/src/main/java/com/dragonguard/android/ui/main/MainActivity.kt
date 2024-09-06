package com.dragonguard.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.data.model.UserInfoModel
import com.dragonguard.android.databinding.ActivityMainBinding
import com.dragonguard.android.ui.compare.SearchCompareRepoFragment
import com.dragonguard.android.ui.login.LoginActivity
import com.dragonguard.android.ui.profile.ClientProfileFragment
import com.dragonguard.android.ui.profile.UserProfileActivity
import com.dragonguard.android.ui.ranking.RankingFragment
import com.dragonguard.android.ui.search.SearchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 사용자의 정보를 보여주고 검색, 랭킹등을
 보러가는 화면으로 이동할 수 있는 메인 activity
 */
class MainActivity : AppCompatActivity() {
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {


        }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var backPressed: Long = 0
    private var loginOut = false
    private var refreshState = true
    private var mainFrag: MainFragment? = null
    private var rankingFrag: RankingFragment? = null
    private var compareFrag: SearchCompareRepoFragment? = null
    private var profileFrag: ClientProfileFragment? = null
    private var imgRefresh = true
    private var realModel = UserInfoModel()
    private var finish = false
    private var post = true

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        finish = false
        Log.d("on", "onnewintent")
        val logout = intent?.getBooleanExtra("logout", false)
        if (logout != null) {
            if (!this@MainActivity.isFinishing) {
                loginOut = logout
                if (loginOut) {
                    binding.mainNav.selectedItemId = binding.mainNav.menu.getItem(0).itemId
                    loginOut = true
                    viewModel.logout()
                    val transaction = supportFragmentManager.beginTransaction()
                    supportFragmentManager.fragments.forEach {
                        transaction.remove(it)
                    }
                    transaction.commit()
                    mainFrag?.let {
                        it.clearView()
                    }
                    mainFrag = null
                    compareFrag = null
                    profileFrag = null
                    rankingFrag = null
                    Log.d("로그인 필요", "로그인 필요")
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    activityResultLauncher.launch(intent)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initObserver()
        //로그아웃시
        if (loginOut) {
            viewModel.logout()
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        binding.mainNav.selectedItemId = binding.mainNav.menu.getItem(0).itemId
        binding.mainLoading.resumeAnimation()
        binding.mainLoading.visibility = View.VISIBLE

        // 유저 정보 가져오기
        //viewModel.getUserInfo()
        refreshMain()

        binding.mainNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_main -> {
                    if (mainFrag != null) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(binding.contentFrame.id, mainFrag!!)
                            .commit()
                    }
                }

                R.id.bottom_rankings -> {
                    rankingFrag = RankingFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(binding.contentFrame.id, rankingFrag!!)
                        .commit()
                }

                R.id.bottom_compare -> {
                    compareFrag = SearchCompareRepoFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(binding.contentFrame.id, compareFrag!!)
                        .commit()
                }

                R.id.bottom_profile -> {
                    Log.d("user name", "user name: ${realModel.github_id}")
                    realModel.github_id?.let {
                        profileFrag = ClientProfileFragment(it)
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(binding.contentFrame.id, profileFrag!!)
                            .commit()
                    }
                }
            }
            true
        }


//        검색창, 검색 아이콘 눌렀을 때 검색화면으로 전환


    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.clickSearch.clicked) {
                        val intent = Intent(applicationContext, SearchActivity::class.java)
                        intent.putExtra("token", "")
                        startActivity(intent)
                    }
                    if (state.newAccessToken.token == null || state.newRefreshToken.refreshToken == null) {
                        if (!this@MainActivity.isFinishing) {
                            Log.d("refresh fail", "token refresh 실패")
                            if (refreshState) {
                                Toast.makeText(
                                    applicationContext,
                                    "다시 로그인 바랍니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                refreshState = false
                                loginOut = true
                                viewModel.logout()
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                activityResultLauncher.launch(intent)
                            }
                        }
                    }

                    if (state.loadState is MainContract.MainState.LoadState.Success) {
                        checkUserInfo(state.userInfo.userInfo)
                    }

                    if (state.clickSearch.clicked) {
                        val intent = Intent(applicationContext, SearchActivity::class.java)
                        intent.putExtra("token", "")
                        startActivity(intent)
                    }

                    if (state.clickUserIcon.clicked) {
                        Log.d("userIcon", "userIcon")
                        val intent = Intent(applicationContext, UserProfileActivity::class.java)
                        intent.putExtra("token", "")
                        intent.putExtra("userName", realModel.github_id)
                        startActivity(intent)
                    }
                }

            }
        }

    }


    private fun refreshMain() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                binding.mainLoading.pauseAnimation()
                binding.mainLoading.visibility = View.GONE
                binding.mainNav.visibility = View.VISIBLE
                mainFrag = MainFragment(realModel, true, viewModel)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(binding.contentFrame.id, mainFrag!!)
                    .commit()
            }

        }
    }

    private fun checkUserInfo(userInfo: UserInfoModel) {
        Log.d("success", "success userInfo: $userInfo")
        if (userInfo.github_id == null) {
            if (!this@MainActivity.isFinishing) {
                Log.d("not login", "login activity로 이동")
                Toast.makeText(applicationContext, "다시 로그인 바랍니다.", Toast.LENGTH_SHORT).show()
                loginOut = true
                viewModel.logout()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                activityResultLauncher.launch(intent)
            }
        } else {
            mainFrag = MainFragment(realModel, imgRefresh, viewModel)
        }
    }

    private fun refreshToken() {
        viewModel.getNewToken()
    }

    /*private fun postCommits() {
            val coroutine2 = CoroutineScope(Dispatchers.Main)
            coroutine2.launch {
                Log.d("post", "post commit")
                if (!this@MainActivity.isFinishing) {
                    val refreshDeffered = coroutine2.async(Dispatchers.IO) {
                        viewModel.postCommits(prefs.getJwtToken(""))
                    }
                    val refreshResult = refreshDeffered.await()
                    Log.d("post", "post token : $token")
                    Handler(Looper.getMainLooper()).postDelayed({ searchUser() }, 2000)
                }
            }
        }*/

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
        finish = false
        loginOut = false
    }

    override fun onPause() {
        super.onPause()
    }


    //    뒤로가기 1번 누르면 종료 안내 메시지, 2번 누르면 종료
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() > backPressed + 2500) {
                backPressed = System.currentTimeMillis()
                Toast.makeText(applicationContext, "Back 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (System.currentTimeMillis() <= backPressed + 2500) {
                finishAffinity()
            }
        }
    }

    fun getNavSize(): Int {
        return binding.mainNav.height
    }

}
