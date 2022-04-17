package com.code.apppraytime.compass

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.code.apppraytime.interfaces.LocationInterface
import java.io.IOException
import java.util.*

class Location(private val context: Context,
               private val locationInterface: LocationInterface
               ) {

//    fun test() {
//            val locMan = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
//            val providers = locMan.getProviders(true)
//            var bestLocation: Location? = null
//            for (provider in providers) {
//                val l = locMan!!.getLastKnownLocation(provider) ?: continue
//                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
//                    // Found best last known location: %s", l);
//                    bestLocation = l
//                }
//            }
//            return bestLocation.
//        }
//
//    }

    suspend fun getCityName(latitude: Double, longitude: Double) {
        kotlin.runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            var country = ""
            var city = ""
            try {
                val addressList: List<Address> = geocoder.getFromLocation(
                    latitude, longitude, 1
                )
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]
                    city = address.locality
                    country = address.countryName
                }
                this
            } catch (e: IOException) {
                Log.e("Location", "Unable connect to Geocoder", e)
            } finally {
                locationInterface.located(city, country)
            }
        }
    }
}