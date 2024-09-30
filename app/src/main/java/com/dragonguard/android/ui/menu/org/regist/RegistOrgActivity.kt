package com.dragonguard.android.ui.menu.org.regist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityRegistOrgBinding
import com.dragonguard.android.ui.menu.MenuActivity
import com.dragonguard.android.util.LoadState
import kotlinx.coroutines.launch

class RegistOrgActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistOrgBinding
    private lateinit var viewModel: RegistOrgViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_regist_org)
        initObserver()
        val arr1: MutableList<String> = mutableListOf("선택하세요")
        arr1.apply {
            add("대학교")
            add("고등학교")
            add("회사")
            add("etc")
        }
        val spinnerAdapter = ArrayAdapter<String>(applicationContext, R.layout.spinner_list, arr1)
        binding.orgTypeSpinner.adapter = spinnerAdapter
        binding.orgTypeSpinner.setSelection(0)


        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.title = "   조직 등록"

        binding.regitstOrgBtn.setOnClickListener {
            if (binding.orgNameEdit.text.toString()
                    .isNotBlank() && binding.orgEmailEdit.text.toString()
                    .isNotBlank() && binding.orgTypeSpinner.selectedItem.toString().isNotBlank()
            ) {
                var orgType = ""
                when (binding.orgTypeSpinner.selectedItem.toString()) {
                    "대학교" -> {
                        orgType = "UNIVERSITY"
                    }

                    "회사" -> {
                        orgType = "COMPANY"
                    }

                    "고등학교" -> {
                        orgType = "HIGH_SCHOOL"
                    }

                    "etc" -> {
                        orgType = "ETC"
                    }
                }
                registOrg(
                    binding.orgNameEdit.text.toString(),
                    orgType,
                    binding.orgEmailEdit.text.toString()
                )
            } else {
                Toast.makeText(
                    applicationContext,
                    "miss!! name: ${binding.orgNameEdit.text}, email: ${binding.orgEmailEdit.text}, type: ${binding.orgTypeSpinner.selectedItem}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.state == LoadState.SUCCESS) {
                        if (state.registResult.result.id != 0L) {
                            Toast.makeText(
                                applicationContext,
                                "${binding.orgNameEdit.text} 등록 요청 완료!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, MenuActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun registOrg(name: String, orgType: String, emailEndPoint: String) {
        viewModel.requestRegistOrg(name, orgType, emailEndPoint)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    //    edittext의 키보드 제거
    fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.orgEmailEdit.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.orgNameEdit.windowToken, 0)
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