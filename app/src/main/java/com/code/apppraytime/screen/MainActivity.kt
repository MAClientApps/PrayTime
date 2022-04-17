package com.code.apppraytime.screen

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.code.apppraytime.R
import com.code.apppraytime.constant.App
import com.code.apppraytime.databinding.ActivityMainBinding
import com.code.apppraytime.factory.HomeViewModelFactory
import com.code.apppraytime.interfaces.AudioUpdate
import com.code.apppraytime.services.AudioService
import com.code.apppraytime.shared.Application
import com.code.apppraytime.singleton.Player
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.*
import com.code.apppraytime.viewModel.HomeViewModel
import java.util.*

class MainActivity : AppCompatActivity(),RewardedVideoCallbacks {

    private lateinit var binding: ActivityMainBinding

    private val indexHelper by lazy { IndexHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigation()
        if (isNetworkAvailable(this)) {
           AdsInitialize(this@MainActivity)
            ShowBannerAds(this@MainActivity, R.id.adsbannenr)
            Appodeal.setRewardedVideoCallbacks(this)
        }
        if (intent.getBooleanExtra("RECREATE", false)) {
            intent.removeExtra("RECREATE")
            binding.bottomNav.selectedItemId = R.id.nav_settings
        }
        initPlayer()
    }

    private fun initPlayer() {
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
                SurahActivity.launch(this@MainActivity, playingSurah, playingAyat)
            }
        }
    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.navController?.let {
            NavigationUI.setupWithNavController(
                binding.bottomNav,
                it
            )
        }

        binding.bottomNav.setOnItemReselectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    ViewModelProvider(this,
                        HomeViewModelFactory()
                    )[HomeViewModel::class.java]
                        .refresh.value = 0
                }
            }
            Log.d("Cause", "This will prevent fragment relaunch")
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(Application(newBase!!).language)
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onStart() {
        super.onStart()
        Player.update = object : AudioUpdate {
            @SuppressLint("SetTextI18n")
            override fun update(surah: Int?, ayah: Int?) {
                binding.musicContainer.visibility =
                    surah?.let {
                        binding.title.text = "Surah ${indexHelper.readSurahX(it)!!.name}, ayah $ayah"
                        View.VISIBLE
                    }?: View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Application(this).run {
            binding.title.text = "Surah ${indexHelper
                .readSurahX(playingSurah)?.name}, ayah $playingAyat"
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

    override fun onRewardedVideoLoaded(isPrecache: Boolean) {
       ShowVideoADS(this)
    }

    override fun onRewardedVideoFailedToLoad() {
    }

    override fun onRewardedVideoShown() {
    }

    override fun onRewardedVideoShowFailed() {
    }

    override fun onRewardedVideoFinished(amount: Double, name: String?) {
    }

    override fun onRewardedVideoClosed(finished: Boolean) {
    }

    override fun onRewardedVideoExpired() {
    }

    override fun onRewardedVideoClicked() {
    }
}