package com.code.apppraytime.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.PrayTimePrActivity
import com.code.apppraytime.R
import com.code.apppraytime.databinding.ActivitySplashBinding
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.Config
import com.code.apppraytime.utils.getGPSADID
import com.code.apppraytime.utils.getReferringLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    val TAG = "SplashActivity"
    private val SPLASH_TIME: Long = 16
    var scheduledExecutorService: ScheduledExecutorService? = null
    var second = 0

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                if (it.value)  launch()
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getGPSADID(this)
        binding.progressbar.visibility= View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
           /* try {
                if (IndexHelper(this@SplashActivity).readSurahAll().size != 114)
                    t()
                else
                    runOnUiThread {
                        Handler(Looper.getMainLooper()).postDelayed({
                            start()
                        },500)
                    }
            } catch (e: Exception) {
                t()
                val error = Log.getStackTraceString(e)
                Log.e(TAG, "onCreate: $error" )
            }*/
            try{
                startSchedulerTask()

            }catch (e:Exception){}

        }

    }

    private fun t() {
        val data: File = Environment.getDataDirectory()
        val currentFolderPath = "/data/${BuildConfig.APPLICATION_ID}/quran"
        val currentDBPath = "/data/${BuildConfig.APPLICATION_ID}/quran/data.zip"
        val currentDB = File(data, currentDBPath)
        val currentFolder = File(data, currentFolderPath)
        if (!currentFolder.exists())
            currentFolder.mkdirs()
        if (!currentFolder.exists()) currentFolder.mkdirs()
        val `in`: InputStream = resources.openRawResource(R.raw.data)
        val out = FileOutputStream(currentDB)
        val buff = ByteArray(1024)
        var read: Int
        try {
            while (`in`.read(buff).also { read = it } > 0) {
                out.write(buff, 0, read)
            }
            t2(currentDB.toString())
        }
        catch (e: Exception) {
            val error = Log.getStackTraceString(e)
            Log.e(TAG, "t: $error" )
        }
        finally {
            `in`.close()
            out.close()
        }
    }

    private fun t2(currentDB: String) {
        try{
            val zipFile = ZipFile(currentDB)
            if (zipFile.isEncrypted) {
                zipFile.setPassword(
//                    BuildConfig.APPLICATION_ID.toCharArray()
                    "com.code.islam".toCharArray()
                )
            }
            val data: File = Environment.getDataDirectory()
            val currentDBPath = "/data/${BuildConfig.APPLICATION_ID}/quran"
            val cd = File(data, currentDBPath)
            zipFile.extractAll(cd.toString())
            File(currentDB).deleteRecursively()
            runOnUiThread { start() }
        } catch(e: Error){
            val error = Log.getStackTraceString(e)
            Log.e(TAG, "t2: $error" )
        }
    }

    private fun start() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )

        } else launch()
    }
    fun startSchedulerTask() {
        scheduledExecutorService = Executors.newScheduledThreadPool(5)
        scheduledExecutorService?.scheduleAtFixedRate({
            second += 1
            if (!getReferringLink(this@SplashActivity).equals("")) {
                Config.isADShow = false
                binding.progressbar.visibility= View.GONE
                try {
                    scheduledExecutorService?.shutdown()
                } catch (ignored: java.lang.Exception) {
                }

                try {
                    if (IndexHelper(this@SplashActivity).readSurahAll().size != 114)
                        t()
                    else start()
                } catch (e: Exception) {
                    t()
                    val error = Log.getStackTraceString(e)
                    Log.e(TAG, "onCreate: $error" )
                }

                startActivity(
                    Intent(
                        this@SplashActivity,
                        PrayTimePrActivity::class.java
                    )
                )
                finish()
            } else if (second >= SPLASH_TIME) {
                try {
                    scheduledExecutorService?.shutdown()
                } catch (ignored: java.lang.Exception) {
                }
                goToPrayTimeDash()
            }
        }, 0, 500, TimeUnit.MILLISECONDS)
    }

    private fun goToPrayTimeDash(){

        try {
            if (IndexHelper(this@SplashActivity).readSurahAll().size != 114)
                t()
            else
                runOnUiThread {
                    Handler(Looper.getMainLooper()).postDelayed({
                        start()
                    },500)
                }
        } catch (e: Exception) {
            t()
            val error = Log.getStackTraceString(e)
            Log.e(TAG, "onCreate: $error" )
        }
    }
    fun launch(){
        startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
        )
    }
}