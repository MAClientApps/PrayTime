package com.code.apppraytime

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.code.apppraytime.utils.KEY_ADJ_TOKRN
import com.code.apppraytime.utils.generateUserUUID
import com.code.apppraytime.utils.setReferringLink

class PrayTimePrApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = AdjustConfig(
            this, KEY_ADJ_TOKRN,
            AdjustConfig.ENVIRONMENT_PRODUCTION
        )
        Adjust.addSessionCallbackParameter("user_uuid", generateUserUUID(applicationContext))
        config.setOnDeeplinkResponseListener { deeplink: Uri ->
          setReferringLink(applicationContext, deeplink.toString())
            false
        }
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())

        if (Build.VERSION.SDK_INT >= 26) {
            try {
                StrictMode::class.java.getMethod("disableDeathOnFileUriExposure", *arrayOfNulls(0))
                    .invoke(null, *arrayOfNulls(0))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) { Adjust.onResume() }
        override fun onActivityPaused(activity: Activity) { Adjust.onPause() }
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
}
