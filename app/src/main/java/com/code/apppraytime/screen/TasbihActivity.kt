package com.code.apppraytime.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.code.apppraytime.R
import com.code.apppraytime.databinding.ActivityTasbihBinding
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ShowBannerAds

class TasbihActivity : AppCompatActivity() {

    private var max = 100
    private var running = 0
    private lateinit var binding: ActivityTasbihBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityTasbihBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.back.setOnClickListener { finish() }
        binding.circularProgress.setProgress(running.toDouble(), max.toDouble())
        ShowBannerAds(this, R.id.adsbannenr)
        binding.thi.setOnClickListener {
            max = 33
            init()
        }
        binding.oneHun.setOnClickListener {
            max = 100
            init()
        }
        binding.oneTha.setOnClickListener {
            max = 1000
            init()
        }
        binding.infy.setOnClickListener {
            max = -1
            init()
        }

        binding.click.setOnClickListener {
            if (max == -1) {
                running++
                binding.circularProgress.setProgress(running.toDouble(), running.toDouble())
            } else if (max!=-1 && running<max) {
                running++
                binding.circularProgress.setProgress(running.toDouble(), max.toDouble())
                if (running == max)
                    Toast.makeText(
                        this, "Completed $max, Click again to reset",
                        Toast.LENGTH_SHORT
                    ).show()
            } else if (running == max) {
                running = 0
                binding.circularProgress.setProgress(running.toDouble(), max.toDouble())
            }
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    private fun init() {
        running = 0
        binding.thi.background = ResourcesCompat.getDrawable(
            resources, R.drawable.tasbih_counter, theme
        )
        binding.oneHun.background = ResourcesCompat.getDrawable(
            resources, R.drawable.tasbih_counter, theme
        )
        binding.oneTha.background = ResourcesCompat.getDrawable(
            resources, R.drawable.tasbih_counter, theme
        )
        binding.infy.background = ResourcesCompat.getDrawable(
            resources, R.drawable.tasbih_counter, theme
        )

        when(max) {
            33 -> binding.thi.background = ResourcesCompat.getDrawable(
                resources, R.drawable.tasbih_counter_select, theme
            )
            100 -> binding.oneHun.background = ResourcesCompat.getDrawable(
                resources, R.drawable.tasbih_counter_select, theme
            )
            1000 -> binding.oneTha.background = ResourcesCompat.getDrawable(
                resources, R.drawable.tasbih_counter_select, theme
            )
            -1 -> binding.infy.background = ResourcesCompat.getDrawable(
                resources, R.drawable.tasbih_counter_select, theme
            )
        }
        binding.circularProgress.setProgress(running.toDouble(), max.toDouble())
    }
}