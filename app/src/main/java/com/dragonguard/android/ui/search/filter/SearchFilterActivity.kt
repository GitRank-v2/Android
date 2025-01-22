package com.dragonguard.android.ui.search.filter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.dragonguard.android.R
import com.dragonguard.android.databinding.ActivitySearchFilterBinding
import com.dragonguard.android.ui.compare.compare.LanguagesAdapter
import com.dragonguard.android.ui.search.SearchActivity
import com.google.android.material.chip.Chip

class SearchFilterActivity : AppCompatActivity(), LanguagesAdapter.OnChipClickListener {
    private lateinit var binding: ActivitySearchFilterBinding
    private val map = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val languages = mutableListOf(
            "C",
            "C#",
            "C++",
            "CoffeeScript ",
            "CSS",
            "Dart",
            "DM",
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
        val extras = mutableListOf("10개 미만", "50개 미만", "100개 미만", "500개 미만", "500개 이상")
        val topics = mutableListOf("0개", "1개", "2개", "3개", "4개 이상")

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        binding.filterUser.setOnClickListener {
            binding.languageFilters.removeAllViews()
            binding.starFilters.removeAllViews()
            binding.topicFilters.removeAllViews()
            binding.forkFilters.removeAllViews()
            binding.filterContent.visibility = View.INVISIBLE
            map.clear()
            map["type"] = "MEMBER"
        }

        binding.filterRepository.setOnClickListener {
            map["type"] = "GIT_REPO"
            binding.filterContent.visibility = View.VISIBLE
        }

        binding.languageFilter.setOnClickListener {
            val bottomSheetFragment = FilterSheetFragment(languages, "language", this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.starFilter.setOnClickListener {
            val bottomSheetFragment = FilterSheetFragment(extras, "stars", this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.forkFilter.setOnClickListener {
            val bottomSheetFragment = FilterSheetFragment(extras, "forks", this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.topicFilter.setOnClickListener {
            val bottomSheetFragment = FilterSheetFragment(topics, "topics", this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.searchName.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                map.put("name", binding.searchName.text.toString())
                val intent = Intent(this@SearchFilterActivity, SearchActivity::class.java)
                intent.putExtra("type", map["type"])
                intent.putExtra("language", map["language"])
                intent.putExtra("stars", map["stars"])
                intent.putExtra("forks", map["forks"])
                intent.putExtra("topics", map["topics"])
                intent.putExtra("name", map["name"])
                setResult(0, intent)
                finish()
            }
            true
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    //    edittext의 키보드 제거
    fun closeKeyboard() {
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

    override fun onOffLanguageClick(type: String, prev: String) {
        for (i in 0 until binding.languageFilters.childCount) {
            val chipL: Chip =
                binding.languageFilters.getChildAt(i) as Chip
            if (chipL.text.toString() == prev) {
                val language =
                    map[type]!!.split(",").toMutableList()
                language.remove(chipL.text.toString())
                val sb = StringBuilder()
                language.forEachIndexed { index, s ->
                    if (index != language.lastIndex) {
                        sb.append("$s,")
                    } else {
                        sb.append(s)
                    }
                }
                map[type] = sb.toString()
                binding.languageFilters.removeView(chipL)
                break // 원하는 Chip을 찾았으므로 반복문을 종료합니다.
            }
        }
    }

    override fun onOffStarClick(type: String, prev: String) {
        for (i in 0 until binding.starFilters.childCount) {
            val chipL: Chip = binding.starFilters.getChildAt(i) as Chip
            Log.d("child", chipL.text.toString())
            if (chipL.text.toString() == prev) {
                map[type] = ""
                binding.starFilters.removeView(chipL)
                break // 원하는 Chip을 찾았으므로 반복문을 종료합니다.
            }
        }
        map[type] = ""
    }

    override fun onOffForkClick(type: String, prev: String) {
        for (i in 0 until binding.forkFilters.childCount) {
            val chipL: Chip = binding.forkFilters.getChildAt(i) as Chip
            if (chipL.text.toString() == prev) {
                map[type] = ""
                binding.forkFilters.removeView(chipL)
                break // 원하는 Chip을 찾았으므로 반복문을 종료합니다.
            }
        }
        map[type] = ""
    }

    override fun onOffTopicClick(type: String, prev: String) {
        for (i in 0 until binding.topicFilters.childCount) {
            val chipL: Chip = binding.topicFilters.getChildAt(i) as Chip
            if (chipL.text.toString() == prev) {
                map[type] = ""
                binding.topicFilters.removeView(chipL)
                break // 원하는 Chip을 찾았으므로 반복문을 종료합니다.
            }
        }
        map[type] = ""
    }

    override fun onOnLanguageClick(chip: Chip, type: String, prev: String) {
        val before = map[type]
        if (!before.isNullOrBlank()) {
            map.put(type, "$before, $prev")
        } else {
            map.put(type, prev)
        }
        chip.setOnClickListener {
            val language = map[type]!!.split(",").toMutableList()
            language.remove(chip.text.toString())
            val sb = StringBuilder()
            language.forEachIndexed { index, s ->
                if (index != language.lastIndex) {
                    sb.append("$s,")
                } else {
                    sb.append(s)
                }
            }
            map[type] = sb.toString()
            binding.languageFilters.removeView(it)
        }
        binding.languageFilters.addView(chip)
    }

    override fun onOnStarClick(chip: Chip, type: String) {
        chip.setOnClickListener {
            map[type] = ""
            binding.starFilters.removeView(it)
        }
        binding.starFilters.addView(chip)
    }

    override fun onOnForkClick(chip: Chip, type: String) {
        chip.setOnClickListener {
            map[type] = ""
            binding.forkFilters.removeView(it)
        }
        binding.forkFilters.addView(chip)
    }

    override fun onOnTopicClick(chip: Chip, type: String) {
        chip.setOnClickListener {
            map[type] = ""
            binding.topicFilters.removeView(it)
        }
        binding.topicFilters.addView(chip)
    }
}