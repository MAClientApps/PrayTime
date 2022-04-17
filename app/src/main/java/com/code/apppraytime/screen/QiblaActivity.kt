package com.code.apppraytime.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.code.apppraytime.R
import com.code.apppraytime.compass.Compass
import com.code.apppraytime.compass.GPSTracker
import com.code.apppraytime.databinding.ActivityQiblaBinding
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.utils.ShowBannerAds
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class QiblaActivity : AppCompatActivity() {

    companion object {
        private const val RC_PERMISSION = 1221
    }

    private val TAG: String = QiblaActivity::class.java.simpleName
    private var compass: Compass? = null

    private var currentAzimuth = 0f
    private var prefs: SharedPreferences? = null
    private var gps: GPSTracker? = null
    private lateinit var binding: ActivityQiblaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityQiblaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }

        prefs = getSharedPreferences("", MODE_PRIVATE)
        gps = GPSTracker(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ShowBannerAds(this, R.id.adsbannenr)
        setupCompass()
    }
    private fun setupCompass() {
        val permissionGranted: Boolean = getBoolean("permission_granted")!!
        if (permissionGranted) {
            getBearing()
        } else {
            binding.angle.text = resources.getString(R.string.msg_permission_not_granted_yet)
//            binding.yourLocation.text = resources.getString(R.string.msg_permission_not_granted_yet)
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                RC_PERMISSION
            )
        }
        compass = Compass(this)
        val cl = Compass.CompassListener { azimuth ->
            adjustGambarDial(azimuth)
            adjustArrowQiblat(azimuth)
        }
        compass?.setListener(cl)

    }

    private fun adjustGambarDial(azimuth: Float) {
        val an: Animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        binding.dial.startAnimation(an)
    }

    private fun adjustArrowQiblat(azimuth: Float) {
        val kiblatDerajat: Float = getFloat("kiblat_derajat")!!
        val an: Animation = RotateAnimation(
            -currentAzimuth + kiblatDerajat, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        binding.qiblaIndicator.startAnimation(an)
        if (kiblatDerajat > 0) {
            binding.qiblaIndicator.visibility = View.VISIBLE
        } else {
            binding.qiblaIndicator.visibility = INVISIBLE
            binding.qiblaIndicator.visibility = View.GONE
        }
    }

    @SuppressLint("MissingPermission")
    fun getBearing() {
        val kaabaDegs: Float = getFloat("kiblat_derajat")!!
        if (kaabaDegs > 0.0001) {
            val strYourLocation: String = if (gps!!.location != null) resources.getString(R.string.your_location) + " " + gps!!.location.latitude + ", " + gps!!.location.longitude else resources.getString(
                R.string.unable_to_get_your_location
            )
//            binding.yourLocation.text = strYourLocation
            val strKaabaDirection: String =
                java.lang.String.format(Locale.ENGLISH, "%.0f", kaabaDegs)
                    .toString() + " " + resources.getString(R.string.degree) + " " + getDirectionString(
                    kaabaDegs
                )
            binding.angle.text = strKaabaDirection
            binding.qiblaIndicator.visibility = View.VISIBLE
        } else {
            fetchGPS()
        }
    }

    private fun getDirectionString(azimuthDegrees: Float): String {
        var where = "NW"
        if (azimuthDegrees >= 350 || azimuthDegrees <= 10) where = "N"
        if (azimuthDegrees < 350 && azimuthDegrees > 280) where = "NW"
        if (azimuthDegrees <= 280 && azimuthDegrees > 260) where = "W"
        if (azimuthDegrees <= 260 && azimuthDegrees > 190) where = "SW"
        if (azimuthDegrees <= 190 && azimuthDegrees > 170) where = "S"
        if (azimuthDegrees <= 170 && azimuthDegrees > 100) where = "SE"
        if (azimuthDegrees <= 100 && azimuthDegrees > 80) where = "E"
        if (azimuthDegrees <= 80 && azimuthDegrees > 10) where = "NE"

        return where
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                saveBoolean("permission_granted", true)
                binding.angle.text = resources.getString(R.string.msg_permission_granted)
//                binding.yourLocation.text = resources.getString(R.string.msg_permission_granted)
                binding.qiblaIndicator.visibility = INVISIBLE
                binding.qiblaIndicator.visibility = View.GONE
                fetchGPS()
            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.toast_permission_required),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun saveBoolean(Judul: String?, bbb: Boolean?) {
        val edit = prefs!!.edit()
        edit.putBoolean(Judul, bbb!!)
        edit.apply()
    }

    private fun getBoolean(Judul: String?): Boolean? {
        return prefs!!.getBoolean(Judul, false)
    }

    private fun saveFloat(Judul: String?, bbb: Float?) {
        val edit = prefs!!.edit()
        edit.putFloat(Judul, bbb!!)
        edit.apply()
    }

    private fun getFloat(Judul: String?): Float? {
        return prefs!!.getFloat(Judul, 0f)
    }

    private fun fetchGPS() {
        val result: Double
        gps = GPSTracker(this)
        if (gps!!.canGetLocation()) {
            val myLat = gps!!.latitude
            val myLng = gps!!.longitude
            // \n is for new line
            val strYourLocation =
                resources.getString(R.string.your_location) + " " + myLat + ", " + myLng
//            binding.yourLocation.text = strYourLocation
            Log.e("TAG", "GPS is on")
            if (myLat < 0.001 && myLng < 0.001) {
                binding.qiblaIndicator.visibility = INVISIBLE
                binding.qiblaIndicator.visibility = View.GONE
                binding.angle.text = resources.getString(R.string.location_not_ready)
//                binding.yourLocation.text = resources.getString(R.string.location_not_ready)
            } else {
                val kaabaLng =
                    39.826206 // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                val kaabaLat =
                    Math.toRadians(21.422487) // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                val myLatRad = Math.toRadians(myLat)
                val longDiff = Math.toRadians(kaabaLng - myLng)
                val y = sin(longDiff) * cos(kaabaLat)
                val x = cos(myLatRad) * sin(kaabaLat) - sin(myLatRad) *
                        cos(kaabaLat) * cos(longDiff)
                result = (Math.toDegrees(atan2(y, x)) + 360) % 360
                saveFloat("kiblat_derajat", result.toFloat())
                val strKaabaDirection: String = java.lang.String.format(
                    Locale.ENGLISH, "%.0f",
                    result.toFloat()
                )
                    .toString() + " " + resources.getString(R.string.degree) + " " + getDirectionString(
                    result.toFloat()
                )
                binding.angle.text = strKaabaDirection
                binding.qiblaIndicator.visibility = View.VISIBLE
            }
        } else {
            gps!!.showSettingsAlert()
            binding.qiblaIndicator.visibility = INVISIBLE
            binding.qiblaIndicator.visibility = View.GONE
            binding.angle.text = resources.getString(R.string.pls_enable_location)
//            binding.yourLocation.text = resources.getString(R.string.pls_enable_location)
        }
    }

    override fun onStart() {
        super.onStart()
        compass?.start(this)
    }

    override fun onPause() {
        super.onPause()
        compass?.stop()
    }

    override fun onResume() {
        super.onResume()
        compass?.start(this)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop compass")
        compass?.stop()
        if (gps != null) {
            gps?.stopUsingGPS()
            gps = null
        }
    }
}