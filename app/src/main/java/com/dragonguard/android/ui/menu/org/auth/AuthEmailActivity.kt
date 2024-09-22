package com.dragonguard.android.ui.menu.org.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityAuthEmailBinding
import com.dragonguard.android.ui.main.MainActivity
import kotlinx.coroutines.launch

class AuthEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthEmailBinding
    private lateinit var viewModel: AuthEmailViewModel
    private var orgName = ""
    private var email = ""
    private var emailAuthId: Long = 0
    private var orgId: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_email)
        viewModel = AuthEmailViewModel()
        initObserver()
        orgId = intent.getLongExtra("orgId", -1)
        email = intent.getStringExtra("email")!!
        orgName = intent.getStringExtra("orgName")!!


        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.title = "   이메일 인증"

        //viewModel.requestAuthEmail(orgId, email)


        binding.resendCode.setOnClickListener {
            if (viewModel.currentState.resetTimer.reset) {
                sendEmail()
            } else {
                deleteEmail()
                sendEmail()
            }
        }

        binding.emailCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 5) {
                    authEmail()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit

        })
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.state is AuthEmailContract.AuthEmailState.LoadState.Success) {
                        Toast.makeText(applicationContext, "$orgName 인증되었습니다!!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                    }

                    if (state.state is AuthEmailContract.AuthEmailState.LoadState.Error) {
                        binding.authStatus.text = "다시 입력해주세요!!"
                        binding.emailCode.setText("")
                    }

                    binding.remainTime.text = state.remainTime.remainTime
                    binding.emailCode.isEnabled = state.timeOver.timeOver
                }
            }
        }
    }

    private fun authEmail() {
        //viewModel.checkEmailCode(emailAuthId, binding.emailCode.text.toString(), orgId)
    }

    private fun deleteEmail() {
        //viewModel.deleteLateEmailCode(emailAuthId)
    }

    private fun sendEmail() {
        //viewModel.sendEmailAuth()

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }


    //    edittext의 키보드 제거
    fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.emailCode.windowToken, 0)
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

        }
        return super.onOptionsItemSelected(item)
    }
}