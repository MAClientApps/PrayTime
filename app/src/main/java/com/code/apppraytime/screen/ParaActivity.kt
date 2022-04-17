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
import com.google.android.material.tabs.TabLayout
import com.code.apppraytime.R
import com.code.apppraytime.adapter.PagerAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.databinding.ActivityParaBinding
import com.code.apppraytime.interfaces.AudioUpdate
import com.code.apppraytime.screen.layout.ParaAyatFrag
import com.code.apppraytime.services.AudioService
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ContextUtils
import java.text.NumberFormat
import java.util.*

class ParaActivity : AppCompatActivity() {

    companion object {
        fun launch(context: Context, paraNo: Int) {
            context.startActivity(
                Intent(
                    context,
                    ParaActivity::class.java
                ).putExtra("PARA_NO", paraNo)
            )
        }
    }

    private val indexHelper by lazy {
        IndexHelper(this)
    }

    private lateinit var binding: ActivityParaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityParaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }

        val numberFormat: NumberFormat =
            NumberFormat.getInstance(Locale(Application(this).language))
        binding.qPager.adapter = PagerAdapter(
            supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ).apply {
            IndexHelper(this@ParaActivity).readParaAll()
                .reversed().forEach {
                    addFragment(ParaAyatFrag(it.id))
                    binding.tabLayout.newTab().setText(
                        resources.getString(R.string.para) +
                                " -> ${numberFormat.format(it.id)}").let { it1 ->
                        binding.tabLayout.addTab(it1)
                    }
                }
        }

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
                SurahActivity.launch(this@ParaActivity, playingSurah, playingAyat)
                finish()
            }
        }

        binding.qPager.offscreenPageLimit = 3
        binding.qPager.currentItem = 30 -
                intent.getIntExtra("PARA_NO", 0)
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(Application(newBase!!).language)
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
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
                        if (Application(this@ParaActivity).playing)
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