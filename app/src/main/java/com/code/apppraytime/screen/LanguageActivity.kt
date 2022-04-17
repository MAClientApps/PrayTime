package com.code.apppraytime.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code.apppraytime.R
import com.code.apppraytime.databinding.ActivityLanguageBinding
import com.code.apppraytime.shared.Application
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ContextUtils
import java.util.*

class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        when(Application(this).language) {
            "en" -> binding.languageGroup.check(R.id.english)
            "bn" -> binding.languageGroup.check(R.id.bangla)
            "tr" -> binding.languageGroup.check(R.id.turkish)
        }

        binding.languageGroup.setOnCheckedChangeListener { group, checkedId ->
            Application(this).language =
                when(checkedId) {
                    R.id.english -> "en"
                    R.id.bangla -> "bn"
                    R.id.turkish -> "tr"
                    else -> "en"
                }
            recreate()
        }

        setResult(Activity.RESULT_OK, Intent())
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(Application(newBase!!).language)
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}