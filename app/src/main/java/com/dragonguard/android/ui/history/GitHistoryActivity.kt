package com.dragonguard.android.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dragonguard.android.R
import com.dragonguard.android.data.model.klip.TokenHistoryModelItem
import com.dragonguard.android.databinding.ActivityGitHistoryBinding
import com.dragonguard.android.ui.main.MainActivity

class GitHistoryActivity : AppCompatActivity(), GitListAdapter.OnHistoryClickListener {
    private lateinit var binding: ActivityGitHistoryBinding
    private var token = ""

    //var viewmodel = Viewmodel()
    private val tokenAdapter by lazy { GitListAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        //callTokenHistory()
    }

    /*
    private fun callTokenHistory() {
        val coroutine = CoroutineScope(Dispatchers.Main)
        coroutine.launch {
            val resultDeferred = coroutine.async(Dispatchers.IO) {
                viewmodel.getTokenHistory(token)
            }
            val result = resultDeferred.await()
            result?.let { it ->
                initRecycler(it)
            }
        }
    }
    */

    private fun initRecycler(result: ArrayList<TokenHistoryModelItem>) {
        binding.tokenContributeList.adapter = tokenAdapter
        binding.tokenContributeList.layoutManager = LinearLayoutManager(this)
        tokenAdapter.submitList(result)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.tokenContributeList.visibility = View.VISIBLE
        }, 500)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.refresh, binding.toolbar.menu)
//        return true
//    }


    //    뒤로가기, 홈으로 화면전환 기능
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

//            R.id.refresh_button -> {
//                if(refresh) {
//                    refresh = false
//                    callTokenHistory()
//                }
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHistoryClick(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        ContextCompat.startActivity(this@GitHistoryActivity, intent, null)
    }
}