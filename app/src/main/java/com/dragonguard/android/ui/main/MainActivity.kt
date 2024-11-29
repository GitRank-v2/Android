package com.dragonguard.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.data.model.main.UserInfoModel
import com.dragonguard.android.databinding.ActivityMainBinding
import com.dragonguard.android.ui.compare.SearchCompareRepoFragment
import com.dragonguard.android.ui.login.LoginActivity
import com.dragonguard.android.ui.profile.user.ClientProfileFragment
import com.dragonguard.android.ui.ranking.outer.RankingFragment
import com.dragonguard.android.util.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 사용자의 정보를 보여주고 검색, 랭킹등을
 보러가는 화면으로 이동할 수 있는 메인 activity
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /*private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {


        }*/

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private var backPressed: Long = 0
    private var refreshState = true
    private var mainFrag: MainFragment? = null
    private var rankingFrag: RankingFragment? = null
    private var compareFrag: SearchCompareRepoFragment? = null
    private var profileFrag: ClientProfileFragment? = null
    private var imgRefresh = true
    private var realModel = UserInfoModel()
    private var finish = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish = false
        Log.d("on", "onnewintent")
        val logout = intent?.getBooleanExtra("logout", false)
        if (logout != null) {
            if (!this@MainActivity.isFinishing) {
                if (logout) {
                    binding.mainNav.selectedItemId = binding.mainNav.menu.getItem(0).itemId
                    viewModel.logout()
                    val transaction = supportFragmentManager.beginTransaction()
                    supportFragmentManager.fragments.forEach {
                        transaction.remove(it)
                    }
                    transaction.commit()
                    mainFrag?.clearView()
                    mainFrag = null
                    compareFrag = null
                    profileFrag = null
                    rankingFrag = null
                    Log.d("로그인 필요", "로그인 필요")
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("mainCreate", "main create")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initObserver()
        //로그아웃시
        this.onBackPressedDispatcher.addCallback(this, callback)
        binding.mainLoading.resumeAnimation()
        binding.mainLoading.visibility = View.VISIBLE
        viewModel.getUserInfo()
        //refreshMain()
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
                    rankingFrag =
                        RankingFragment(viewModel.currentState.userInfo.userInfo.github_id!!)
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
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loadState == LoadState.SUCCESS) {
                        Log.d("success UserInfo", "success")
                        viewModel.setFinish()
                        checkUserInfo(state.userInfo.userInfo)
                    }

                    if (state.loadState == LoadState.LOGIN_FAIL) {
                        refreshToken()
                    }

                    if (state.loadState == LoadState.IMAGE_LOADED) {
                        Log.d("image", "image loaded")
                        binding.mainLoading.visibility = View.GONE
                        binding.mainLoading.pauseAnimation()
                    }
                }

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is MainContract.MainActivityEffect.LoginError -> {
                            Log.d("error", "Login Error")
                            binding.mainNav.selectedItemId = binding.mainNav.menu.getItem(0).itemId
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
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }


    private fun checkUserInfo(userInfo: UserInfoModel) {
        Log.d("success", "success userInfo: $userInfo")
        if (userInfo.github_id.isNullOrEmpty()) {
            if (!this@MainActivity.isFinishing) {
                Log.d("not login", "login activity로 이동")
                Toast.makeText(applicationContext, "다시 로그인 바랍니다.", Toast.LENGTH_SHORT).show()
                viewModel.logout()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {

            if (finish) {
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                finish = true
                withContext(Dispatchers.Main) {
                    if (mainFrag == null) {
                        mainFrag =
                            MainFragment(
                                viewModel.currentState.userInfo.userInfo,
                                imgRefresh,
                                viewModel
                            )
                    }
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(binding.contentFrame.id, mainFrag!!)
                        .commit()
                }
            }
        }
    }

    private fun refreshToken() {
        viewModel.getNewToken()
    }


    override fun onRestart() {
        super.onRestart()
        if (binding.mainNav.selectedItemId == R.id.bottom_main) {
            viewModel.setRepeat(false)
            finish = false
            viewModel.refreshAmount()
            viewModel.getUserInfo()
        }
    }


    //    뒤로가기 1번 누르면 종료 안내 메시지, 2번 누르면 종료
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.mainNav.selectedItemId == R.id.bottom_main) {
                if (System.currentTimeMillis() > backPressed + 2500) {
                    backPressed = System.currentTimeMillis()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.finish_app),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return
                } else {
                    finishAffinity()
                }
            } else {
                binding.mainNav.selectedItemId = R.id.bottom_main
                Log.d("back", "back to mainfrag")
            }
        }
    }

    fun getNavSize(): Int {
        return binding.mainNav.height
    }

}
