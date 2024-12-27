package com.dragonguard.android.ui.menu.org.approval

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivityApprovalOrgBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApprovalOrgActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApprovalOrgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_approval_org)


        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        supportActionBar?.title = "조직 등록 요청 승인"


        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.approve_org_list, ApproveOrgFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.approve_request_btn -> {
                    supportActionBar?.title = "승인 대기중인 조직 목록"
                    val transactionN = supportFragmentManager.beginTransaction()
                    transactionN.replace(R.id.approve_org_list, ApproveOrgFragment())
                        .commit()
                }

                R.id.approve_finished_btn -> {
                    supportActionBar?.title = "승인된 조직 목록"
                    val transactionN = supportFragmentManager.beginTransaction()
                    transactionN.replace(R.id.approve_org_list, ApprovedOrgFragment())
                        .commit()
                }
            }
            true
        }
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