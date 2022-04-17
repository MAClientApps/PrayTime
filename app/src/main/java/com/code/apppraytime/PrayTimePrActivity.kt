package com.code.apppraytime

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.code.apppraytime.utils.Config
import com.code.apppraytime.utils.generatePremiumDeepLink
import io.bidmachine.core.Utils

class PrayTimePrActivity : AppCompatActivity() {
    private var portalWeb: WebView? = null
    var ErrorLayout: LinearLayout? = null
    var TryBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_praytime_preium)
        setupView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupView() {
        portalWeb = findViewById(R.id.webview_praytime)
        ErrorLayout = findViewById(R.id.errorlayout)
        CookieManager.getInstance().setAcceptCookie(true)
        portalWeb?.settings?.javaScriptEnabled = true
        portalWeb?.settings?.useWideViewPort = true
        portalWeb?.settings?.loadWithOverviewMode = true
        portalWeb?.settings?.domStorageEnabled = true
        portalWeb?.settings?.pluginState = WebSettings.PluginState.ON
        portalWeb?.webChromeClient = WebChromeClient()
        portalWeb?.visibility = View.VISIBLE
        portalWeb?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                val url = request.url.toString()
                try {
                    if (url.startsWith(getString(R.string.app_scheme)) && Uri.parse(url).query
                            ?.isNotEmpty() == true
                    ) {
                        Config.isADShow = false
                    }
                } catch (e: Exception) {
                }
                if (!url.startsWith("http")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    } catch (ignored: Exception) {
                    }
                }
                finish()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
        callLoadView()
    }

    fun manageInternetCoon() {
        ErrorLayout?.visibility = View.VISIBLE
        TryBtn = findViewById(R.id.trybutton)
        TryBtn?.setOnClickListener(View.OnClickListener { view: View? ->
            ErrorLayout?.visibility = View.GONE
            callLoadView()
        })
    }

    private fun callLoadView() {
        if (Utils.isNetworkAvailable(this)) {
            portalWeb?.loadUrl(generatePremiumDeepLink(this))
        } else {
            manageInternetCoon()
        }
    }

    public override fun onResume() {
        super.onResume()
        portalWeb?.onResume()
    }

    public override fun onPause() {
        super.onPause()
        portalWeb?.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        portalWeb?.loadUrl("about:blank")
    }

    override fun onBackPressed() {

    }
}
