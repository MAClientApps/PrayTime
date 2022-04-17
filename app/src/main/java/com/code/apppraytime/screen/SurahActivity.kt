package com.code.apppraytime.screen

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentPagerAdapter
import com.code.apppraytime.singleton.Player
import com.code.apppraytime.R
import com.google.android.material.tabs.TabLayout
import com.code.apppraytime.adapter.PagerAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.constant.Name.surahNames
import com.code.apppraytime.shared.LastRead
import com.code.apppraytime.databinding.ActivitySurahBinding
import com.code.apppraytime.interfaces.AudioUpdate
import com.code.apppraytime.screen.layout.SurahAyat
import com.code.apppraytime.services.AudioService
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ContextUtils
import com.code.apppraytime.utils.ShowBannerAds
import java.util.*

class SurahActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context, surahNo: Int, ayat: Int?) {
            context.startActivity(
                Intent(
                    context,
                    SurahActivity::class.java
                ).putExtra("SURAH_NO", surahNo)
                    .putExtra("AYAT", ayat)
            )
        }
    }

    private val indexHelper by lazy {
        IndexHelper(this)
    }

    private var surahNo: Int? = null
    private lateinit var binding: ActivitySurahBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivitySurahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            if (intent.getBooleanExtra("NOTI", false))
                startActivity(
                    Intent(
                        this,
                        SplashActivity::class.java
                    )
                )
            finish()
        }
        ShowBannerAds(this, R.id.adsbannenr)
        val ayat = intent.getIntExtra("AYAT", 0)
        surahNo = intent.getIntExtra("SURAH_NO", 0)

        binding.qPager.adapter = PagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ).apply {
            for (x in 114 downTo 1) {
                addFragment(
                    SurahAyat().also {
                        it.arguments = Bundle().apply {
                            putInt("position", x)
                            putInt("ayat", ayat)
                            putBoolean("scroll", x==surahNo)
                        }
                    }
                )
                surahNames[x-1].let {
                    binding.tabLayout.addTab(
                        binding.tabLayout.newTab().setText(it)
                    )
                }
            }
        }

        LastRead(this@SurahActivity).surahNo = surahNo!!

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.qPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout)
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                /****/
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                /****/
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.qPager.currentItem = tab.position
                LastRead(this@SurahActivity).run {
                    surahNo = 114 - tab.position
                    surahName = surahNames[113-tab.position]
                }
            }
        })

        binding.playPause.setOnClickListener {
            binding.playPause.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    if (Application(this).playing) {
                        sendBroadcast(
                            Intent(App.PAUSE)
                        )
                        R.drawable.ic_play_arrow
                    } else {
                        sendBroadcast(
                            Intent(App.RESUME)
                        )
                        R.drawable.ic_pause
                    }, null
                )
            )
        }

        binding.close.setOnClickListener {
            sendBroadcast(
                Intent(App.STOP)
            )
        }

        binding.musicContainer.setOnClickListener {
            Application(this).run {
                launch(this@SurahActivity, playingSurah, playingAyat)
                finish()
            }
        }

        binding.qPager.offscreenPageLimit = 3
        binding.qPager.setCurrentItem(114 - surahNo!!, false)
    }


    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(Application(newBase!!).language)
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getBooleanExtra("NOTI", false))
            startActivity(
                Intent(
                    this,
                    SplashActivity::class.java
                )
            )
    }

    override fun onStart() {
        super.onStart()
        Player.update = object : AudioUpdate {
            @SuppressLint("SetTextI18n")
            override fun update(surah: Int?, ayah: Int?) {
                Log.e("Start", "YES")
                binding.musicContainer.visibility =
                    surah?.let {
                        binding.title.text = "Surah ${indexHelper.readSurahX(it)!!.name}, ayah $ayah"
                        View.VISIBLE
                    }?: View.GONE
                binding.playPause.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        if (Application(this@SurahActivity).playing)
                            R.drawable.ic_pause
                        else
                            R.drawable.ic_play_arrow
                        , null
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Application(this).run {
            binding.title.text = "Surah ${indexHelper
                .readSurahX(playingSurah)!!.name}, ayah $playingAyat"
        }

        binding.musicContainer.visibility =
            if (serviceRunning()) View.VISIBLE else View.GONE
        binding.playPause.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                if (Application(this).playing)
                    R.drawable.ic_pause
                else
                    R.drawable.ic_play_arrow
                , null
            )
        )
    }

    private fun serviceRunning(): Boolean {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)
            ?.getRunningServices(Int.MAX_VALUE)?.forEach {
                if (AudioService::class.java.name == it.service.className)
                    return true
            }
        return false
    }
}