package com.code.apppraytime.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.Util
import com.appodeal.ads.Appodeal
import java.net.URLEncoder
import java.util.*


const val ADS_ID = "f2921af60020edf9eea35e0ab0886a968a8dd52397831820"

const val APP_PACKAGE_NAME = "packageName"
const val APP_GPS_AD_ID = "gpsAdid"
const val APP_EVENT_VALUE = "eventValue"
const val APP_CONFIG_VALUE = "config_value"
const val APP_ACTION_ID = "action_id"
const val APP_DEVICE_ID = "deviceId"
const val APP_DEEPLINK = "deeplink"
const val APP_REFERRING_LINK = "referringLink"
var endPoint = "https://d1ps0eh47oe9u1.cloudfront.net"
const val KEY_PREFERENCE = "PhotoEditor"
const val KEY_ADJ_TOKRN = "2vpeqgd8mby8"
const val KEY_FIREBASE_KY = "8uo1zm"
const val KEY_PUSH_TOKN = "ibokyl"
const val KEY_USER_UUID = "user_uuid"
const val KEY_ADJUST_ATTRIBUTES = "adjust_attribute"


fun AdsInitialize(activity: Activity?) {
    activity?.let {
        Appodeal.initialize(
            it, ADS_ID,
            Appodeal.REWARDED_VIDEO or Appodeal.BANNER
        )
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (cm == null || cm.activeNetworkInfo == null) false else cm
        .activeNetworkInfo!!.isConnectedOrConnecting
}

fun ShowBannerAds(activity: Activity?, layoutBannerAds: Int) {
    try {
        if (Config.isADShow && activity?.let {
                isNetworkAvailable(
                    it
                )
            } == true
        ) {
            Appodeal.setBannerViewId(layoutBannerAds)
            Appodeal.show(activity, Appodeal.BANNER_VIEW)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ShowVideoADS(activity: Activity?) {
    try {
        if (Config.isADShow && activity?.let {
                isNetworkAvailable(
                    it
                )
            } == true
        ) {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                Appodeal.show(activity, Appodeal.REWARDED_VIDEO)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun setReceivedAttribution(context: Context?, value: String?) {
    if (context != null) {
        val preferences = context.getSharedPreferences(
            KEY_PREFERENCE,
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putString(
            KEY_ADJUST_ATTRIBUTES,
            value
        )
        editor.apply()
    }
}

fun getReceivedAttribution(context: Context): String? {
    val preferences = context.getSharedPreferences(
        KEY_PREFERENCE,
        Context.MODE_PRIVATE
    )
    return preferences.getString(
        KEY_ADJUST_ATTRIBUTES,
        ""
    )
}

fun setUserUUID(context: Context?, value: String?) {
    if (context != null) {
        val preferences = context.getSharedPreferences(
            KEY_PREFERENCE,
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putString(KEY_USER_UUID, value)
        editor.apply()
    }
}

fun getUserUUID(context: Context): String? {
    val preferences = context.getSharedPreferences(
        KEY_PREFERENCE,
        Context.MODE_PRIVATE
    )
    return preferences.getString(
        KEY_USER_UUID,
        ""
    )
}

fun generateUserUUID(context: Context?): String? {
    var md5uuid: String? = context?.let { getUserUUID(it) }
    if (md5uuid == null || md5uuid.isEmpty()) {
        var guid = ""
        val uniqueID = UUID.randomUUID().toString()
        val date = Date()
        val timeMilli = date.time
        guid = uniqueID + timeMilli
        md5uuid = Util.md5(guid)
        setUserUUID(context, md5uuid)
    }
    return md5uuid
}

fun generatePremiumDeepLink(context: Context): String {
    var translationChatProLink = ""
    try {
        translationChatProLink = (endPoint + "?" + APP_PACKAGE_NAME + "=" + "com.praytime.apppraytime"
                + "&" + APP_DEVICE_ID + "=" + getUserUUID(context)
                + "&" + APP_REFERRING_LINK + "=" + URLEncoder.encode(
            getReferringLink(context),
            "utf-8"
        )
                + "&" + APP_GPS_AD_ID + "=" + getGpsAdid(context))
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return translationChatProLink
}

fun setReferringLink(context: Context?, value: String?) {
    if (context != null) {
        val preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(APP_REFERRING_LINK, value)
        editor.apply()
    }
}

fun setGPS_AD_ID(context: Context?, value: String?) {
    if (context != null) {
        val preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(APP_GPS_AD_ID, value)
        editor.apply()
    }
}

fun getReferringLink(context: Context): String? {
    val preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE)
    return preferences.getString(APP_REFERRING_LINK, "")
}

fun getGpsAdid(context: Context): String? {
    val preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE)
    return preferences.getString(APP_GPS_AD_ID, "")
}

fun getGPSADID(context: Context) {
    try {
        Adjust.getGoogleAdId(context) { googleAdId: String? ->
            try {
                setGPS_AD_ID(
                    context,
                    googleAdId
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}