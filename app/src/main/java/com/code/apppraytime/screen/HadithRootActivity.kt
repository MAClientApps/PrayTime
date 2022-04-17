package com.code.apppraytime.screen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code.apppraytime.databinding.ActivityHadithRootBinding
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.theme.ApplicationTheme

class HadithRootActivity : AppCompatActivity(), HomeInterface {

    companion object {
        fun launch(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    HadithRootActivity::class.java
                )
            )
        }
    }

    private lateinit var binding: ActivityHadithRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityHadithRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }



    }

    override fun onBottom(id: String) {

    }
}