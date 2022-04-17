package com.code.apppraytime.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code.apppraytime.databinding.ActivityStyleBinding
import com.code.apppraytime.shared.Application
import com.code.apppraytime.theme.ApplicationTheme

class StyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (Application(this).layoutStyle==0) {
            binding.style1.isChecked = true
            binding.style2.isChecked = false
        } else {
            binding.style1.isChecked = false
            binding.style2.isChecked = true
        }

        binding.style1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.style2.isChecked = false
                Application(this).layoutStyle = 0
            }
        }

        binding.style2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.style1.isChecked = false
                Application(this).layoutStyle = 1
            }
        }
    }
}