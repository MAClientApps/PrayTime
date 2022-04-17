package com.code.apppraytime.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import android.os.Bundle
import com.code.apppraytime.R
import android.location.Location
import android.widget.Toast
import android.net.ConnectivityManager
import com.google.android.gms.maps.CameraUpdateFactory
import android.util.Log
import org.json.JSONObject
import org.json.JSONException
import android.widget.ImageView
import com.google.android.gms.maps.model.*
import com.code.apppraytime.shared.Application
import com.code.apppraytime.theme.ApplicationTheme
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.graphics.drawable.Drawable
import android.net.NetworkCapabilities
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.code.apppraytime.constant.App.API_KEY
import com.code.apppraytime.databinding.ActivityMosqueBinding
import com.code.apppraytime.utils.ShowBannerAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.gms.maps.SupportMapFragment
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.constants.ProviderType
import com.yayandroid.locationmanager.listener.LocationListener

@SuppressLint("MissingPermission")
open class MosqueActivity : AppCompatActivity(), LocationListener {

//    private var grab = false
    private var refreshClick = 0
    private var googleMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var userMarker: Marker? = null
    private var updateFinished = true
    private var markers: Array<Marker?>?= null
    private var lastKnownLocation: Location? = null
    private var markerOptions: Array<MarkerOptions?>? = null

    private val awesomeConfiguration by lazy {
        LocationConfiguration.Builder()
            .keepTracking(true)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Gimme the permission!")
                    .requiredPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    .build()
            ).useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .requiredTimeInterval((1 * 60 * 1000).toLong())
                    .requiredDistanceInterval(0)
                    .acceptableAccuracy(5.0f)
                    .acceptableTimePeriod((1 * 60 * 1000).toLong())
                    .gpsMessage("Turn on GPS?")
                    .setWaitPeriod(ProviderType.GPS, (20 * 1000).toLong())
                    .setWaitPeriod(ProviderType.NETWORK, (20 * 1000).toLong())
                    .build()
            ).build()
    }

    private val awesomeConfiguration2 by lazy {
        LocationConfiguration.Builder()
            .keepTracking(true)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Gimme the permission!")
                    .requiredPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    .build()
            ).useGooglePlayServices (
                GooglePlayServicesConfiguration.Builder()
                    .fallbackToDefault(true)
                    .askForGooglePlayServices(false)
                    .askForSettingsApi(true)
                    .failOnSettingsApiSuspended(false)
                    .ignoreLastKnowLocation(false)
                    .setWaitPeriod(20 * 1000)
                    .build()
                )
            .build()
    }

    private lateinit var binding: ActivityMosqueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityMosqueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wifiReceiver = WifiReceiver()
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(wifiReceiver, intentFilter)
        findViewById<ImageView>(R.id.back)
            .setOnClickListener { finish() }
        binding.frameLayout.isEnabled = false
//        locMan = getSystemService(LOCATION_SERVICE) as LocationManager
        ShowBannerAds(this, R.id.adsbannenr)
        googleMap?.let {
            googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            markers = arrayOfNulls(MAX_PLACES)
        }?: run {
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync { map ->
                    googleMap = map
                    if (Application(this).darkTheme)
                        googleMap!!.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.mapstyle_night
                            )
                        )

                    googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

                    markers = arrayOfNulls(MAX_PLACES)
                    enableGPS()
                }
        }

        binding.refresh.setOnClickListener {
            if (refreshClick<2) {
                binding.refresh.visibility = View.GONE
                binding.locLoad.visibility = View.VISIBLE
                locationManager = LocationManager.Builder(applicationContext)
                    .activity(this)
                    .configuration(awesomeConfiguration)
                    .notify(this)
                    .build()
                locationManager?.get()
            }
        }
    }

    private fun enableGPS() {
        locationManager = LocationManager.Builder(applicationContext)
            .activity(this)
            .configuration(awesomeConfiguration2)
            .notify(this)
            .build()
        locationManager?.get()
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

    private fun updatePlaces(loc: Location?) {
        var lat = 0.0
        var lng = 0.0
        if (loc != null) {
            lat = loc.latitude
            lng = loc.longitude
        }

        if (loc != null) {
            binding.frameLayout.isEnabled = true

            val lastLatLng = LatLng(lat, lng)

            if (userMarker != null)
                userMarker!!.remove()
            userMarker = googleMap!!.addMarker(
                MarkerOptions()
                    .position(lastLatLng)
                    .title("You are here")
                    .snippet("Your last recorded location")
            )
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15f))

            val placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                    "json?location=" + lat + "," + lng +
                    "&radius=1000&sensor=true" +
                    "&types=mosque" +
                    "&key=$API_KEY"
            if (isNetworkAvailable)
                execute(placesSearchStr)
            else Toast.makeText(
                this,
                "No internet access, app will not work properly",
                Toast.LENGTH_LONG
            ).show()
        } else
            Toast.makeText(
                this,
                "Location could not be found, please enable location and allow app to use location and internet.",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun process(vararg params: String?): String {
        updateFinished = false
        val placesBuilder = StringBuilder()
        for (placeSearchURL in params) {
            try {
                val requestUrl = URL(placeSearchURL)
                val connection = requestUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    var reader: BufferedReader?
                    val inputStream = connection.inputStream ?: return ""
                    reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        placesBuilder.append(
                            """ $line """.trimIndent()
                        )
                    }
                    if (placesBuilder.isEmpty()) {
                        return ""
                    }
                    Log.d("test", placesBuilder.toString())
                } else {
                    Log.i("test", "Unsuccessful HTTP Response Code: $responseCode")
                    return ""
                }
            } catch (e: MalformedURLException) {
                Log.e("test", "Error processing Places API URL", e)
                return ""
            } catch (e: IOException) {
                Log.e("test", "Error connecting to Places API", e)
                return ""
            }
        }
        return placesBuilder.toString()
    }

    private fun execute(data: String?) {
        CoroutineScope(Dispatchers.Default).launch {
            process(data).let { result->
            if (markers != null) {
                runOnUiThread {
                    for (pm in markers!!.indices) {
                        if (markers!![pm] != null) markers!![pm]!!.remove()
                    }
                }
            }
            try {
                val resultObject = JSONObject(result)
                val placesArray = resultObject.getJSONArray("results")
                markerOptions = arrayOfNulls(placesArray.length())
                for (p in 0 until placesArray.length()) {
                    var missingValue: Boolean
                    var placeLL: LatLng? = null
                    var placeName = ""
                    var vicinity = ""
                    try {
                        missingValue = false
                        val placeObject = placesArray.getJSONObject(p)
                        val loc = placeObject.getJSONObject("geometry")
                            .getJSONObject("location")
                        placeLL = LatLng(
                            java.lang.Double.valueOf(loc.getString("lat")),
                            java.lang.Double.valueOf(loc.getString("lng"))
                        )
                        val types = placeObject.getJSONArray("types")
                        for (t in 0 until types.length()) {
                            val thisType = types[t].toString()
                            if (thisType.contains("mosque")) {
                                break
                            } else if (thisType.contains("health")) {
                                break
                            } else if (thisType.contains("doctor")) {
                                break
                            }
                        }
                        vicinity = placeObject.getString("vicinity")
                        placeName = placeObject.getString("name")
                    } catch (jse: JSONException) {
                        Log.v("PLACES", "missing value")
                        runOnUiThread {
                            Toast.makeText(
                                this@MosqueActivity,
                                "Could not fetch data from server",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        missingValue = true
                        jse.printStackTrace()
                    }

                    runOnUiThread {
                        val drawable = ResourcesCompat.getDrawable(
                            resources, R.drawable.mosq, null
                        ) as Drawable

                        if (missingValue) markerOptions!![p] = null else markerOptions!![p] =
                            MarkerOptions()
                                .position(placeLL!!)
                                .title(placeName)
                                .snippet(vicinity)
                                .icon(BitmapDescriptorFactory.fromBitmap(drawable.toBitmap()))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@MosqueActivity,
                        "$e",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            if (markerOptions != null && markers != null) {
                runOnUiThread {
                    var p = 0
                    while (p < markerOptions!!.size && p < markers!!.size) {
                        if (markerOptions!![p] != null) {
                            markers!![p] = googleMap!!.addMarker(markerOptions!![p]!!)
                        }
                        p++
                    }
                }
            }
            updateFinished = true
            }
        }
    }

    override fun onProcessTypeChanged(processType: Int) {
        Log.e("onProcessTypeChanged", processType.toString())
    }

    override fun onLocationChanged(location: Location?) {
        Log.e("Trigger", refreshClick.toString())
        binding.locLoad.visibility = View.GONE
        binding.refresh.visibility = View.VISIBLE
        location?.let { loc->
            lastKnownLocation?.let {
                if ((loc.latitude-it.latitude) > 0.001
                    || (loc.latitude-it.latitude) < -0.001
                    || (loc.longitude-it.longitude) > 0.001
                    || (loc.longitude-it.longitude) < -0.001) {
                        lastKnownLocation = location
                        updatePlaces(lastKnownLocation)
                        refreshClick++
                }
            } ?: kotlin.run {
                lastKnownLocation = location
                updatePlaces(lastKnownLocation)
                refreshClick++
            }
        } ?: kotlin.run {
            lastKnownLocation = location
            updatePlaces(lastKnownLocation)
            refreshClick++
        }

//        if(!grab) {
//            grab = true
//            locationManager = LocationManager.Builder(applicationContext)
//                .activity(this)
//                .configuration(awesomeConfiguration)
//                .notify(this)
//                .build()
//
//            locationManager?.get()
//        }
    }

    override fun onLocationFailed(type: Int) {
        Log.e("onLocationFailed", type.toString())
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean) {
        Log.e("onPermissionGranted", alreadyHadPermission.toString())
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e("onStatusChanged", provider.toString())
    }

    override fun onProviderEnabled(provider: String?) {
        Log.e("onProviderEnabled", provider.toString())
    }

    override fun onProviderDisabled(provider: String?) {
        Log.e("onProviderDisabled", provider.toString())
    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isNetworkAvailable && googleMap != null && markers == null)
                updatePlaces(null)
            else if (!isNetworkAvailable)
                Toast.makeText(
                    this@MosqueActivity,
                    "Please enable internet, or the app will not work properly.",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
        locationManager?.cancel()
//        locMan!!.removeUpdates(this)
    }

    companion object {
        private const val MAX_PLACES = 20
        var wifiReceiver: WifiReceiver? = null
    }
}