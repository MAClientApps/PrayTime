package com.code.apppraytime.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.code.apppraytime.R
import com.code.apppraytime.adapter.PlayerAdapter
import com.code.apppraytime.constant.App
import com.code.apppraytime.constant.App.API_KEY
import com.code.apppraytime.databinding.ActivityPlayerBinding
import com.code.apppraytime.factory.PlayerViewModelFactory
import com.code.apppraytime.interfaces.HomeInterface
import com.code.apppraytime.model.VideoChildModel
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.viewModel.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PlayerActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener, HomeInterface {

    companion object {
        fun launch(context: Context, pos: Int, title: String,
                   id: String, data: ArrayList<VideoChildModel?>) {
            context.startActivity(
                Intent(
                    context,
                    PlayerActivity::class.java
                ).putExtra("TITLE", title)
                    .putExtra("DATA", data)
                    .putExtra("POS", pos)
                    .putExtra("ID", id)
            )
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this, PlayerViewModelFactory()
        )[PlayerViewModel::class.java]
    }

    private val playerAdapter by lazy {
        PlayerAdapter(this, viewModel, this)
    }

    private var processing = true
    private var touchActive = false
    private var timer: Timer? = null
    private var player: YouTubePlayer? = null

    private val layoutManager by lazy {
        LinearLayoutManager(this)
    }

    private val smoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return 80f / displayMetrics!!.densityDpi
                //super.calculateSpeedPerPixel(displayMetrics)
            }
        }
    }

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        setContentView(binding.root)
        binding.backButton.clipToOutline = true
        binding.backButton.setOnClickListener {
            timer?.cancel()
            timer?.purge()
            player?.pause()
            player?.release()
            player = null
            finish()
        }

        binding.videoTitle.text = intent.getStringExtra("TITLE")

        if (viewModel.videoList.value == null) {
            viewModel.videoList.value =
                intent.getParcelableArrayListExtra("DATA")

            viewModel.videoPosition.value =
                intent.getIntExtra("POS", 0)

            loadPlaylists()
        }

        try {
            viewModel.milliSec.value = player?.currentTimeMillis
        } catch (e: Exception){
            Log.e("Player Exception", "$e")
        }

        binding.playPause.setOnClickListener {
            player?.run {
                try {
                    if ((currentTimeMillis+1000)<durationMillis) {
                        if (isPlaying) pause()
                        else play()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@PlayerActivity,
                        "$e", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.milliSec.observe(this) {
            binding.currentProgress.text = calculateTime(it ?: 0)
            binding.videoProgress.progress = it ?: 0
        }

        viewModel.videoPosition.observe(this) {
            viewModel.milliSec.value = 0
            viewModel.videoList.value?.get(it)?.let { lcm ->
                player?.cueVideo(lcm.videoUrl)
            }

            smoothScroller.targetPosition = it
            binding.playerRecycler.post {
                layoutManager.startSmoothScroll(smoothScroller)
            }

            binding.videoPosition.text = "${it + 1}/${viewModel.videoList.value?.size ?: 0}"
        }

        binding.videoProgress.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    player?.seekToMillis((progress*1000))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.d("Seek", "onStartTrackingTouch")
                    touchActive = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.progress?.let {
                        player?.seekToMillis(it * 1000)
                    }
                    touchActive = false
                }
            })

        binding.fullScreen.setOnClickListener {
            player?.let {
                //fullScreen = true
                it.setFullscreen(true)
                viewModel.fullScreen.value = true
            }
        }

        binding.playerRecycler.layoutManager = layoutManager
        binding.playerRecycler.adapter = playerAdapter

        (supportFragmentManager
            .findFragmentById(R.id.player_fragment)
                as YouTubePlayerSupportFragmentX)
            .initialize(API_KEY, this)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean) {
        player = p1

        if (viewModel.videoList.value?.size ?: 0 > 0) {
            p1?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
            viewModel.videoList.value?.get(viewModel.videoPosition.value ?: 0)?.videoUrl
            p1?.cueVideo(viewModel.videoList.value!![viewModel.videoPosition.value!!]?.videoUrl)
            p1?.setPlayerStateChangeListener(playerState)
            p1?.setPlaybackEventListener(playerEvent)
        } else {
            Toast.makeText(
                this,
                "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
        }

        p1?.setOnFullscreenListener {
            p1.setPlayerStyle(
                if (it) {
                    YouTubePlayer.PlayerStyle.DEFAULT
                }
                else {
                    YouTubePlayer.PlayerStyle.CHROMELESS
                }
            )
        }
    }

    private var playerEvent = object : YouTubePlayer.PlaybackEventListener {
        override fun onPlaying() {
            Log.d("Log", "onPlaying")
            binding.playPause.setImageDrawable(
                ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_pause, null))
//            App().writeProgress(courseId.toString(), Progress().apply {
//                id = courseId?: 0
//                last = viewModel?.coursePosition?.value?: 0
//                //Commented line will be live on Production
//                //watchTime = prev+((System.currentTimeMillis()-currentWatchTime)/60000)
//                watchTime = App().readProgressAt(courseId!!)?.watchTime?: 0
//            })

        }

        override fun onPaused() {
//            val prev = Nelu().readProgressAt(courseId!!)?.watchTime?: 0
//            Nelu().writeProgress(courseId.toString(), Progress().apply {
//                id = courseId ?: 0
//                last = viewModel?.coursePosition?.value ?: 0
//                //Commented line will be live on Production
//                //watchTime = prev+((System.currentTimeMillis()-currentWatchTime)/60000)
//                watchTime = prev + ((System.currentTimeMillis() - currentWatchTime) / 6000)
//            })
            binding.playPause.setImageDrawable(ResourcesCompat.getDrawable(resources,
                R.drawable.ic_play_arrow, null))
//            currentWatchTime = 0L
//            Log.e("Progress", Nelu().readProgressAt(courseId!!)?.watchTime.toString())
        }

        override fun onStopped() {
            Log.d("Log", "onStopped")
        }

        override fun onBuffering(p0: Boolean) {
            Log.d("Log", "onBuffering")
        }

        override fun onSeekTo(p0: Int) {
            Log.d("Log", "onSeekTo")
        }
    }

    private val playerState = object : YouTubePlayer.PlayerStateChangeListener {
        override fun onLoading() {
            //Calls onLoading
        }

        override fun onLoaded(p0: String?) {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.milliSec.value?.let {
                    player?.seekToMillis(it*1000)
                }
                player?.play()
            }, 100)
            player?.durationMillis?.let {
                binding.maxProgress.text = calculateTime((it/1000)-1)//"${it/1000}"
                binding.videoProgress.max = (it/1000)-1
            }
            binding.playPause.setImageDrawable(ResourcesCompat.getDrawable(resources,
                R.drawable.ic_play_arrow, null))
        }

        override fun onAdStarted() {
            //OnLoaded
        }

        override fun onVideoStarted() {
            Log.e("Log", "Video Started")
        }

        override fun onVideoEnded() {
            binding.playPause.setImageDrawable(
                ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_play_arrow, null))
        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {
            Log.e("Player Activity", "$p0")
            p0?.name?.let {
                Toast.makeText(
                    this@PlayerActivity,
                    it, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?) {
        Toast.makeText(this, p1.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun calculateTime(prog: Int): String {
        var timeText: String
        var hour = 0
        var min = 0
        val sec: Int
        if (prog>59) {
            min = prog/60
            sec = prog%50
            if (min>59) {
                hour = min/60
                min %= 60
            }
        } else {
            sec = prog
        }
        if (hour != 0) {
            timeText = if (hour>9)
                "$hour"
            else "0$hour"
            timeText = if (min>9)
                "$timeText : $min"
            else "$timeText : 0$min"
        } else {
            timeText = if (min>9)
                "$min"
            else "0$min"
            timeText = if (sec>9)
                "$timeText : $sec"
            else "$timeText : 0$sec"
        }
        return timeText
    }

    private fun monitor() {
        timer?.cancel()
        timer?.purge()
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        if (player != null && player!!.isPlaying && !touchActive) {
                            player?.currentTimeMillis?.let {
                                viewModel.milliSec.value = it / 1000
                                if ((player?.durationMillis ?: 0) < (it + 1000)) {
                                    player?.pause()
                                    if (viewModel.videoPosition.value!! < viewModel.videoList.value!!.size) {
                                        viewModel.videoPosition.value =
                                            viewModel.videoPosition.value!! + 1
                                        playerAdapter.update(viewModel.videoPosition.value!!)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Player Exception", "$e")
//                    CustomToast(this@PlayerActivity)
//                        .show("$e", CustomToast.TOAST_NEGATIVE)
                    }
                }
            }
        }

        timer = Timer()
        timer?.schedule(timerTask, 0L, 1000L)
    }

    private fun loadPlaylists() {
        CoroutineScope(Dispatchers.Default).launch {
            (if (viewModel.videoList.value!!.size<2) FirebaseDatabase.getInstance()
                .getReference("video")
                .child("data").child(intent.getStringExtra("ID")!!)
                .orderByKey().limitToFirst(App.LOAD_ITEM_PER_QUERY)
            else FirebaseDatabase.getInstance().getReference("video")
                .child("data").child(intent.getStringExtra("ID")!!)
                .orderByKey().startAfter(
                    viewModel.videoList.value!![viewModel.videoList.value!!.size-2]?.position.toString()
                ).limitToFirst(App.LOAD_ITEM_PER_QUERY))
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        @SuppressLint("SetTextI18n")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var hasData = false
                            val size = viewModel.videoList.value!!.size
                            viewModel.videoList.value?.removeAt(size-1)
                            runOnUiThread {
                                playerAdapter.notifyItemRemoved(size - 1)
                            }
                            snapshot.children.forEach { snap->
                                viewModel.videoList.value?.add(
                                    VideoChildModel(
                                        snap.key!!.toInt(),
                                        snap.child("title").value.toString(),
                                        snap.child("url").value.toString()
                                    )
                                )
                                hasData = true
                            }
                            binding.videoPosition.text = "${viewModel.videoPosition.value!!+1}" +
                                    "/${viewModel.videoList.value?.size}"
                            if (hasData) viewModel.videoList.value?.add(null)
                            runOnUiThread {
                                playerAdapter.notifyItemRangeInserted(
                                    size, viewModel.videoList.value!!.size - size
                                )
                                processing = false
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@PlayerActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
        }
    }

    override fun onBackPressed() {
        if (viewModel.fullScreen.value == null) {
            player?.pause()
            timer?.cancel()
            timer?.purge()
            super.onBackPressed()
        }
        viewModel.fullScreen.value?.let {
            if (it) {
                viewModel.fullScreen.value = false
                player?.setFullscreen(false)
            } else super.onBackPressed()
        }
        player?.currentTimeMillis?.let {
            viewModel.milliSec.value = it
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Default).launch { monitor() }
    }

    override fun onPause() {
        player?.pause()
        timer?.cancel()
        timer?.purge()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBottom(id: String) {
        if(!processing) {
            processing = true
            loadPlaylists()
        }
    }
}