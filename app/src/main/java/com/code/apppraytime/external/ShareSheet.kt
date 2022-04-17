package com.code.apppraytime.external

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import com.code.apppraytime.R
import com.code.apppraytime.shared.Application
import com.code.apppraytime.tajweed.exporter.Exporter
import com.code.apppraytime.tajweed.exporter.HtmlExporter
import com.code.apppraytime.tajweed.model.Result
import com.code.apppraytime.tajweed.model.ResultUtil
import com.code.apppraytime.tajweed.model.TajweedRule
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ShareSheet(val context: Context) {

    fun share(ayat: String, translation: String,
              ayatNo: String, text: String) {
        val shareSheet = BottomSheetDialog(context, if (Application(context).darkTheme)
            R.style.bottomSheetDark else R.style.bottomSheet)

        shareSheet.setContentView(R.layout.sheet_share)

        val arabic = shareSheet.findViewById<TextView>(R.id.arabic)
        shareSheet.findViewById<TextView>(R.id.ayat_pos)?.text = ayatNo
        val transla = shareSheet.findViewById<TextView>(R.id.translation)
        val tajweed = shareSheet.findViewById<SwitchMaterial>(R.id.taj_weed)!!

        tajweed.visibility =
            if (Application(context).tajweed)
            View.VISIBLE else View.GONE

        arabic?.text = if (tajweed.isChecked) html(ayat) else ayat
        transla?.text = translation

        shareSheet.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                val layoutParams = layout.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                layout.layoutParams = layoutParams

                behaviour.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            stateChange(newState, behaviour)
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            Log.e("TAG", "Slide")
                        }
                    }
                )

                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        shareSheet.findViewById<SeekBar>(R.id.arabic_seek)
            ?.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val size = (progress+16).toFloat()
                        arabic?.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        /** OnStart Tracking */
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        /** OnStop Tracking */
                    }

                }
            )

        shareSheet.findViewById<SeekBar>(R.id.translation_seek)
            ?.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val size = (progress+12).toFloat()
                        transla?.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        /** OnStart Tracking */
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        /** OnStop Tracking */
                    }

                }
            )

        tajweed.setOnCheckedChangeListener { buttonView, isChecked ->
            arabic?.text = if (isChecked) html(ayat) else ayat
        }

        val render = shareSheet.findViewById<LinearLayout>(R.id.render)!!

        shareSheet.findViewById<Button>(R.id.share_txt)
            ?.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
        }

        shareSheet.findViewById<Button>(R.id.share_img)
            ?.setOnClickListener {
                Bitmap.createBitmap(
                    render.width*2, render.height*2, Bitmap.Config.ARGB_8888
                ).let {
                    render.draw(Canvas(it).apply {
                        scale(2.0f,2.0f)
                    })
                    shareBitmap(it)
                }
            }

        shareSheet.findViewById<TextView>(R.id.closet)?.run {
            clipToOutline = true
            setOnClickListener {
                shareSheet.dismiss()
            }
        }

        shareSheet.show()
    }

    private fun stateChange(newState: Int, behaviour: BottomSheetBehavior<View>) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            (behaviour as BottomSheetBehavior<*>).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun html(text: String): Spanned {
        val exporter: Exporter = HtmlExporter()
        val results: MutableList<Result> = java.util.ArrayList()
        TajweedRule.MADANI_RULES.forEach {
            results.addAll(it.rule.checkAyah(text))
        }
        ResultUtil.INSTANCE.sort(results)
        Log.e("Check ->", exporter.export(text, results))
        return HtmlCompat.fromHtml(exporter.export(text, results), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(context.externalCacheDir, "share/")
        cachePath.mkdirs()

        val file = File(cachePath, "share.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val contentUri: Uri = getUriForFile(
            context, (context as Activity)
                .applicationContext.packageName + ".provider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.type = "image/png"
        intent.clipData = ClipData.newRawUri("", contentUri)

        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        context.startActivity(Intent.createChooser(intent, "Share..."))
    }
}