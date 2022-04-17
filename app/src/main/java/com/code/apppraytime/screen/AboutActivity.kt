package com.code.apppraytime.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.code.apppraytime.constant.Dev.EMAIL
import com.code.apppraytime.constant.Dev.FACEBOOK
import com.code.apppraytime.constant.Dev.FACEBOOK_WEB
import com.code.apppraytime.constant.Dev.GITHUB
import com.code.apppraytime.constant.Dev.INSTAGRAM
import com.code.apppraytime.constant.Dev.INSTAGRAM_WEB
import com.code.apppraytime.constant.Dev.PHONE
import com.code.apppraytime.constant.Dev.TWITTER
import com.code.apppraytime.constant.Dev.TWITTER_WEB
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.databinding.ActivityAboutBinding
import com.code.apppraytime.theme.ApplicationTheme

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.closeAbout.setOnClickListener { finish() }
        binding.by.text = HtmlCompat.fromHtml(
            "by <span style=\"color:${ApplicationTheme(this)
                .getAccentColor()}\">Nelu Code</span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.aboutVersion.text = "V - ${BuildConfig.VERSION_NAME}"

        binding.aboutFacebook.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.facebook.katana", 0)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK))
                i.setPackage("com.facebook.katana")
                startActivity(i)
            } catch (e: java.lang.Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(FACEBOOK_WEB)
                    )
                )
            }
        }

        binding.aboutInstagram.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.instagram.android", 0)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM))
                i.setPackage("com.instagram.android")
                startActivity(i)
            } catch (e: java.lang.Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_WEB)
                    )
                )
                Log.println(Log.ASSERT, "error", e.toString())
            }
        }

        binding.aboutTwitter.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.twitter.android", 0)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER))
                i.setPackage("com.twitter.android")
                startActivity(i)
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(TWITTER_WEB)
                    )
                )
                Log.e("error", "$e")
            }
        }

        binding.aboutGithub.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.github.android", 0)
                val i = Intent(
                    Intent.ACTION_VIEW, Uri.parse(GITHUB)
                )
                i.setPackage("com.github.android")
                startActivity(i)
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(GITHUB)
                    )
                )
                Log.e("error", "$e")
            }
        }

        binding.aboutEmail.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",
                    EMAIL, null
                )
            )
            startActivity(Intent.createChooser(intent, "Send email..."))
        }

        binding.aboutCall.setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CALL_PHONE), 9)
        }

        binding.aboutWhatsapp.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.setPackage("com.whatsapp")
                i.data = Uri.parse("https://api.whatsapp.com/send?phone=$PHONE")
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(this, "WhatsApp is not installed in your phone", Toast.LENGTH_SHORT).show()
//                CustomToast(this).show("WhatsApp is not installed in your phone"
//                    , CustomToast.TOAST_NEGATIVE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$PHONE")
            startActivity(callIntent)
        }
    }

//    override fun attachBaseContext(newBase: Context?) {
//        val localeToSwitchTo = Locale(Application(newBase!!).language)
//        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
//        super.attachBaseContext(localeUpdatedContext)
//    }
}