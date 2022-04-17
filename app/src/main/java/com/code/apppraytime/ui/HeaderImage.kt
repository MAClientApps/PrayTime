package com.code.apppraytime.ui

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.code.apppraytime.R
import com.code.apppraytime.shared.Application
import com.code.apppraytime.databinding.FragmentHomeBinding

class HeaderImage(private val context: Context) {

    fun loadHeaderImages(binding: FragmentHomeBinding) {
        context.run {
            val p = (1 * resources.displayMetrics.density).toInt()
            //Padding
            /*binding.quran.setPadding(p, p, p, p)
            binding.hadith.setPadding(p, p, p, p)
            binding.dua.setPadding(p, p, p, p)
            binding.mosque.setPadding(p, p, p, p)
            binding.qibla.setPadding(p, p, p, p)
            //Load Images
            Glide.with(this).load(App.QURAN)
                    .centerCrop().into(binding.quran)
            Glide.with(this).load(App.HADIS)
                    .centerCrop().into(binding.hadith)
            Glide.with(this).load(App.DUA)
                    .centerCrop().into(binding.dua)
            Glide.with(this).load(App.TASBIH)
                    .centerCrop().into(binding.mosque)
            Glide.with(this).load(App.QIBLA)
                    .centerCrop().into(binding.qibla)*/
        }
    }

    fun loadHeaderVector(binding: FragmentHomeBinding) {
        context.run {
            val p = (8 * resources.displayMetrics.density).toInt()
            //Padding
            /*binding.quran.setPadding(p, p, p, p)
            binding.hadith.setPadding(p, p, p, p)
            binding.dua.setPadding(p, p, p, p)
            binding.mosque.setPadding(p, p, p, p)
            binding.qibla.setPadding(p, p, p, p)
            //Color Filter
            binding.quran.setColorFilter(getColor())
            binding.hadith.setColorFilter(getColor())
            binding.dua.setColorFilter(getColor())
            binding.mosque.setColorFilter(getColor())
            binding.qibla.setColorFilter(getColor())*/
        }
    }

    private fun getColor(): Int {
        return ResourcesCompat.getColor(
            context.resources,
            when(Application(context).primaryColor) {
                Application.GREEN -> R.color.green
                Application.BLUE -> R.color.blue
                Application.ORANGE -> R.color.orange
                else -> R.color.green
            }, null
        )
    }
}