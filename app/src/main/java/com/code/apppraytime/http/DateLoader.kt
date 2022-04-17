package com.code.apppraytime.http

import android.app.Activity
import android.util.Log
import com.code.apppraytime.screen.layout.Home
import com.code.apppraytime.viewModel.HomeViewModel
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class DateLoader(private val activity: Activity?, private val viewModel: HomeViewModel) {

    fun calculateDate() {
        runCatching {
            var son : JSONObject? = null
            val url = URL("http://api.aladhan.com/v1/gToH?date=${
                SimpleDateFormat(Home.datePattern, Locale.US).format(Date())
            }")

            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            try {
                val buff =
                    BufferedReader(InputStreamReader(BufferedInputStream(urlConnection.inputStream)))
                val dta = StringBuilder()
                var chunks: String?
                while (buff.readLine().also { chunks = it } != null) {
                    dta.append(chunks)
                }
                son = JSONObject(dta.toString())
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            } finally {
                urlConnection.disconnect()
                val timings = son?.getJSONObject("data")?.getJSONObject("hijri")
                activity?.runOnUiThread {
                    viewModel.date.value = timings?.getString("day")+
                            " "+timings?.getJSONObject("month")?.getString("en")+
                            "\n"+timings?.getString("year")+" "+
                            timings?.getJSONObject("designation")?.getString("abbreviated")
                }
            }
        }
    }

}