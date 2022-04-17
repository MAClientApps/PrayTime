package com.code.apppraytime.screen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.code.apppraytime.R
import com.code.apppraytime.databinding.ActivityPrayerTimeBinding
import com.code.apppraytime.shared.SalatTimes
import com.code.apppraytime.theme.ApplicationTheme

class PrayerTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrayerTimeBinding
    private val salatTimes: SalatTimes by lazy {
        SalatTimes(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityPrayerTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        initText()

        binding.madhab.check(
            if (salatTimes.hanafi)
                R.id.hanafi else R.id.shafi
        )

        binding.madhab.setOnCheckedChangeListener { _, checkedId ->
            salatTimes.hanafi = checkedId == R.id.hanafi
        }

        binding.prayerGroup.check(
            when(salatTimes.calculation) {
                SalatTimes.MUSLIM_WORLD_LEAGUE -> R.id.mwl
                SalatTimes.EGYPTIAN -> R.id.egyptian
                SalatTimes.KARACHI -> R.id.karachi
                SalatTimes.UMM_AL_QURA -> R.id.umm_al_qura
                SalatTimes.DUBAI -> R.id.dubai
                SalatTimes.MOON_SIGHTING_COMMITTEE -> R.id.msc
                SalatTimes.NORTH_AMERICA -> R.id.na
                SalatTimes.KUWAIT -> R.id.kuwait
                SalatTimes.QATAR -> R.id.qatar
                SalatTimes.SINGAPORE -> R.id.singapur
                else -> R.id.karachi
            }
        )

        binding.prayerGroup.setOnCheckedChangeListener { _, checkedId ->
            salatTimes.putCalculation(
                when(checkedId) {
                    R.id.mwl -> SalatTimes.MUSLIM_WORLD_LEAGUE
                    R.id.egyptian -> SalatTimes.EGYPTIAN
                    R.id.karachi -> SalatTimes.KARACHI
                    R.id.umm_al_qura -> SalatTimes.UMM_AL_QURA
                    R.id.dubai -> SalatTimes.DUBAI
                    R.id.msc -> SalatTimes.MOON_SIGHTING_COMMITTEE
                    R.id.na -> SalatTimes.NORTH_AMERICA
                    R.id.kuwait -> SalatTimes.KUWAIT
                    R.id.qatar -> SalatTimes.QATAR
                    R.id.singapur -> SalatTimes.SINGAPORE
                    else -> SalatTimes.KARACHI
                }
            )
        }

        intent.getIntExtra("SCROLL", 0).let {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.nScroll.scrollBy(0, it)
            },10)
        }

        setResult(Activity.RESULT_OK, Intent())
    }

    private fun initText() {
        binding.mwl.text = convertText(
            "Muslim World League", "18", "17"
        )
        binding.egyptian.text = convertText(
            "Egyptian General Authority", "19.5", "17.5"
        )
        binding.karachi.text = convertText(
            "Islamic University, Karachi", "18", "18"
        )
        binding.ummAlQura.text = convertText(
            "Umm Al-Qura University, Makkah", "18.5","90 min"
        )
        binding.dubai.text = convertText(
            "The Gulf Region, Dubai", "18.2","18.2"
        )
        binding.msc.text = convertText(
            "Moonsighting Committee", "18","18"
        )
        binding.na.text = convertText(
            "ISNA method (not recommended)", "15","15"
        )
        binding.kuwait.text = convertText(
            "Kuwait", "18","17.5"
        )
        binding.qatar.text = convertText(
            "Qatar (Modified version of Umm al-Qura)", "18", "18"
        )
        binding.singapur.text = convertText(
            "Singapore", "20", "18"
        )
    }

    private fun convertText(first: String, fajr: String, isha: String): Spanned {
        val accentColor = ApplicationTheme(this).getAccentColor()
        return HtmlCompat.fromHtml(
            "<b>$first</b><br>Angle of Fajr " +
                    "<span style=\"color:$accentColor\">$fajr</span> and Isha " +
                    "<span style=\"color:$accentColor\">$isha</span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}