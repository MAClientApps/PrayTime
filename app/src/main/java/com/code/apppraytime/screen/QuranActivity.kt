package com.code.apppraytime.screen

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentPagerAdapter
import com.code.apppraytime.singleton.Player
import com.google.android.material.tabs.TabLayout
import com.code.apppraytime.R
import com.code.apppraytime.adapter.PagerAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.shared.LastRead
import com.code.apppraytime.databinding.ActivityQuranBinding
import com.code.apppraytime.external.Search
import com.code.apppraytime.interfaces.AudioUpdate
import com.code.apppraytime.screen.layout.BookmarkFrag
import com.code.apppraytime.screen.layout.ParaFrag
import com.code.apppraytime.screen.layout.SurahFrag
import com.code.apppraytime.services.AudioService
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ShowBannerAds
import java.text.NumberFormat
import java.util.*

class QuranActivity : AppCompatActivity() {

    private val indexHelper by lazy {
        IndexHelper(this)
    }

    private lateinit var binding: ActivityQuranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityQuranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.headerCard.clipToOutline = true
        binding.back.setOnClickListener { finish() }

        binding.quranPager.adapter = PagerAdapter(
            supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ).apply {
            addFragment(SurahFrag())
            addFragment(ParaFrag())
            addFragment(BookmarkFrag())
        }
        ShowBannerAds(this, R.id.adsbannenr)
        binding.quranPager.offscreenPageLimit = 3
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.quranPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(binding?.tabLayout)
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) { /****/ }

            override fun onTabUnselected(tab: TabLayout.Tab?) { /****/ }

            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.quranPager.currentItem = tab.position
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
                SurahActivity.launch(this@QuranActivity, playingSurah, playingAyat)
                finish()
            }
        }

        binding.search.setOnClickListener {
            Search(this).searchSheet()
        }
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
                        if (Application(this@QuranActivity).playing)
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

        binding.let {
            LastRead(this).let { last->
                Log.e("POS", last.surahNo.toString())
                it.surahName.text = resources.getStringArray(R.array.surah_name)[last.surahNo-1] //last.surahName
                it.ayahNo.text =
                    if (last.ayatNo == 0) "${resources.getString(R.string.ayat_no)}: " +
                            NumberFormat.getInstance(Locale(Application(this).language))
                                .format(1)
                    else "${resources.getString(R.string.ayat_no)}: " +
                            NumberFormat.getInstance(Locale(Application(this)
                                .language)).format(last.ayatNo)
                it.headerCard.setOnClickListener { _ ->
                    SurahActivity.launch(this, last.surahNo, last.ayatNo)
                }

                it.continueBt.setOnClickListener { _ ->
                    SurahActivity.launch(this, last.surahNo, last.ayatNo)
                }
            }
        }
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