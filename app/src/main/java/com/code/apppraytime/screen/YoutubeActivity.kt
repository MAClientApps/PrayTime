package com.code.apppraytime.screen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.code.apppraytime.R
import com.code.apppraytime.constant.App
import com.code.apppraytime.databinding.ActivityYoutubeBinding
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX

class YoutubeActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {

    companion object {
        fun launch(context: Context, id: String) {
            context.startActivity(
                Intent(
                    context, YoutubeActivity::class.java
                ).putExtra("ID", id)
            )
        }
    }

    private lateinit var binding: ActivityYoutubeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        binding = ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (supportFragmentManager
            .findFragmentById(R.id.player_fragment)
                as YouTubePlayerSupportFragmentX)
            .initialize(App.API_KEY, this)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
//        p1?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
        p1?.cueVideo(intent.getStringExtra("ID"))
        p1?.setFullscreen(true)
        p1?.setPlayerStateChangeListener(
            object : YouTubePlayer.PlayerStateChangeListener {
                override fun onLoading() {
                }

                override fun onLoaded(p0: String?) {
                    p1.play()
                }

                override fun onAdStarted() {
                }

                override fun onVideoStarted() {
                }

                override fun onVideoEnded() {
                    finish()
                }

                override fun onError(p0: YouTubePlayer.ErrorReason?) {
                }

            }
        )
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Toast.makeText(
            this,
            p1?.name.toString(),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}