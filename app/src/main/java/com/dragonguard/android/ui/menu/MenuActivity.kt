package com.dragonguard.android.ui.menu

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityMenuBinding
import com.dragonguard.android.ui.main.MainActivity
import com.dragonguard.android.ui.menu.criterion.CriterionActivity
import com.dragonguard.android.ui.menu.faq.FaqActivity
import com.dragonguard.android.ui.menu.org.approval.ApprovalOrgActivity
import com.dragonguard.android.ui.menu.org.auth.AuthOrgActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
 메인화면에서 프로필 사진이나 id를 눌렀을 때 메뉴를 보여주는 activity
 */
@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private val viewModel by viewModels<MenuViewModel>()
    private lateinit var versionDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()

        viewModel.checkAdmin()
        versionDialog = Dialog(this)
        versionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        versionDialog.setContentView(R.layout.version_dialog)
        val version = versionDialog.findViewById<TextView>(R.id.gitrank_version)
        version.append("v2.0.0")

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)


//        로그아웃버튼 누르면 로그아웃 기능
        binding.logout.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("logout", true)
            startActivity(intent)
        }

//        faq버튼 누르면 faq 화면으로 전환
        binding.faq.setOnClickListener {
            val intent = Intent(applicationContext, FaqActivity::class.java)
            startActivity(intent)
        }

//        토큰부여기준버튼 누르면 화면 전환
        binding.tokenCriterion.setOnClickListener {
            val intent = Intent(applicationContext, CriterionActivity::class.java)
            startActivity(intent)
        }

//        버전버튼 누르면 dialog 띄움
        binding.version.setOnClickListener {
            versionDialog.show()
        }

        binding.organizationAuth.setOnClickListener {
            val intent = Intent(applicationContext, AuthOrgActivity::class.java)

            startActivity(intent)
        }
        binding.organizationApprove.setOnClickListener {
            val intent = Intent(applicationContext, ApprovalOrgActivity::class.java)
            startActivity(intent)
        }
        binding.withdrawBtn.setOnClickListener {
            viewModel.withDrawAccount()
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    Log.d("MenuActivity", "initObserver: ${state.admin.isAdmin}")
                    if (state.admin.isAdmin) {
                        binding.adminFun.visibility = View.VISIBLE
                    } else {
                        binding.adminFun.visibility = View.GONE
                    }

                    if (state.withDraw.isSuccess) {
                        withDraw()
                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun withDraw() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("logout", true)
            startActivity(intent)
        }, 1000)
    }
}