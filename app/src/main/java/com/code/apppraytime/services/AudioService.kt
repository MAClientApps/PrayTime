package com.code.apppraytime.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.code.apppraytime.singleton.Player
import com.code.apppraytime.constant.App
import com.code.apppraytime.constant.App.PAUSE
import com.code.apppraytime.constant.App.PLAY
import com.code.apppraytime.constant.App.RESUME
import com.code.apppraytime.constant.App.STOP
import com.code.apppraytime.notification.Foreground
import com.code.apppraytime.shared.Application
import java.lang.Exception

class AudioService : Service(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener {

    private var startReceiver: BroadcastReceiver? = null
    private var stopReceiver: BroadcastReceiver? = null
    private var pauseReceiver: BroadcastReceiver? = null
    private var resumeReceiver: BroadcastReceiver? = null

    class LocalBinder : Binder() {
        val service: AudioService
            get() = AudioService()
    }

    private var surah = 0
    private var current = 0
    private val TAG = "Audio Service"
    private var playList = ArrayList<String>()
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null

    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            surah = it.getIntExtra("SURAH", 0)
            current = it.getIntExtra("CURRENT", 0)
            mediaPlayer = MediaPlayer()
            playList = it.getStringArrayListExtra("LIST")!!
            if (mediaPlayer == null) initMediaPlayer()
            if (requestAudioFocus()) {
                playAudio()
                Application(this@AudioService).run {
                    playing = true
                    playingSurah = surah+1
                    playingAyat = current+1
                }
            } else stopSelf()
        }

        startReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    Application(this@AudioService)
                        .playing = true
                    surah = it.getIntExtra("SURAH", 0)
                    current = it.getIntExtra("CURRENT", 0)
                    if (mediaPlayer == null) mediaPlayer = MediaPlayer()
                    playList = it.getStringArrayListExtra("LIST")!!
                    if (requestAudioFocus()) {
                        playAudio()
                        Application(this@AudioService).run {
                            playing = true
                            playingSurah = surah+1
                            playingAyat = current+1
                        }
                    } else stopSelf()
                }
            }
        }

        stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    Application(this@AudioService)
                        .playing = false
                    mediaPlayer?.pause()
                    Player.update?.update(null, null)
                    stopForeground(true)
                    stopSelf()
                }
            }
        }

        pauseReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    Application(this@AudioService)
                        .playing = false
                    mediaPlayer?.pause()
//                    MainActivity.update?.update(0)
//                    stopForeground(true)
//                    stopSelf()
                }
            }
        }

        resumeReceiver  = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {Application(this@AudioService)
                    .playing = true
                    mediaPlayer?.start()
                    Log.e("Call", "OnResume")
//                    MainActivity.update?.update(0)
//                    stopForeground(true)
//                    stopSelf()
                }
            }
        }

        registerReceiver(stopReceiver, IntentFilter(STOP))
        registerReceiver(startReceiver, IntentFilter(PLAY))
        registerReceiver(pauseReceiver, IntentFilter(PAUSE))
        registerReceiver(resumeReceiver, IntentFilter(RESUME))

        startForeground(101, Foreground(this).generateForegroundNotification())

        return super.onStartCommand(intent, flags, startId)
    }

    fun playAudio() {
        sendBroadcast(
            Intent(App.SURAH+surah)
                .putExtra("AYAT", current)
        )
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(this, Uri.parse(playList[current]))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            Application(this@AudioService).run {
                playing = true
                playingSurah = surah+1
                playingAyat = current+1
            }
            Player.update?.update(surah+1, current+1)
            mediaPlayer?.setOnCompletionListener {
                current++
                if (playList.size>current) {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(this, Uri.parse(playList[current]))
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    sendBroadcast(
                        Intent(App.SURAH+surah)
                            .putExtra("AYAT", current)
                    )
                    Application(this@AudioService).run {
                        playing = true
                        playingSurah = surah+1
                        playingAyat = current+1
                    }
                    Player.update?.update(surah+1, current+1)
                } else {
                    Application(this@AudioService)
                        .playing = false
                    Player.update?.update(null, null)
                    stopForeground(true)
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            Application(this@AudioService)
                .playing = false
            Log.e("Service Error", "$e")
            Player.update?.update(null, null)
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnBufferingUpdateListener(this)
        mediaPlayer?.setOnSeekCompleteListener(this)
        mediaPlayer?.setOnInfoListener(this)
        mediaPlayer?.reset()

        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager?.abandonAudioFocus(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onError")
        return true
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer == null) initMediaPlayer() else if (!mediaPlayer!!.isPlaying) mediaPlayer!!.start()
                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.setVolume(0.1f, 0.1f)
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        Log.d(TAG, "onSeekComplete")
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onInfo")
        return true
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        Log.d(TAG, "onBufferingUpdate")
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAudioFocus()
        unregisterReceiver(stopReceiver)
        unregisterReceiver(startReceiver)
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
    }
}