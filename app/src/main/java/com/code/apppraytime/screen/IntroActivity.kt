package com.code.apppraytime.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code.apppraytime.databinding.ActivityIntroBinding
import com.code.apppraytime.theme.ApplicationTheme

class IntroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        setContentView(binding.root)
    }
}