package com.code.apppraytime.screen.layout

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.R
import com.code.apppraytime.constant.Dev.EMAIL
import com.code.apppraytime.databinding.FragmentSettingsBinding
import com.code.apppraytime.external.TACPP
import com.code.apppraytime.factory.HomeViewModelFactory
import com.code.apppraytime.factory.LearnViewModelFactory
import com.code.apppraytime.screen.*
import com.code.apppraytime.shared.Application
import com.code.apppraytime.shared.SystemData
import com.code.apppraytime.viewModel.HomeViewModel
import com.code.apppraytime.viewModel.LearnViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class Settings : Fragment() {

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when(result.resultCode) {
                RESULT_OK -> {
                    reCreate()
                }
            }
        }

    private val coroutine by lazy {
        CoroutineScope(Dispatchers.Default)
    }

    private val applicationData by lazy {
        Application(requireContext())
    }
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initViewItemClip()

        binding.language.setOnClickListener {
            startForResult.launch(Intent(requireContext(), LanguageActivity::class.java))
        }

        binding.style.setOnClickListener {
            startActivity(
                Intent(requireContext(), StyleActivity::class.java)
            )
        }

        binding.salatTime.setOnClickListener {
            startForResult.launch(Intent(requireContext(), PrayerTimeActivity::class.java))
        }

        binding.translation.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    TranslationActivity::class.java
                )
            )
        }

        binding.tajweedSwi.isChecked = applicationData.tajweed
        binding.switchTheme.isChecked = applicationData.darkTheme

        binding.tajweedSwi.setOnCheckedChangeListener { _, isChecked ->
            applicationData.tajweed = isChecked
        }

        binding.switchTheme.setOnCheckedChangeListener{ _, isChecked ->
            applicationData.darkTheme = isChecked
            reCreate()
        }

        binding.arabicSizeText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP, Application(requireContext()).arabicFontSize
        )
        binding.transliterationSizeText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP, Application(requireContext()).transliterationFontSize
        )
        binding.translationSizeText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP, Application(requireContext()).translationFontSize
        )
        binding.fontSize.setOnClickListener {
            if (binding.fontSizeExpandable.isExpanded) {
                binding.openFont.animate()
                    .rotation(0f)
                    .start()
                binding.openFont.rotation = 0f
                binding.fontSizeExpandable.collapse()
            }
            else {
                binding.openFont.animate()
                    .rotation(180f)
                    .start()
                binding.openFont.rotation = 180f
                binding.fontSizeExpandable.expand()
            }
        }

        binding.termsCondition.setOnClickListener {
            TACPP(requireActivity()).launch(1)
        }

        binding.rate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)))
        }

        binding.policy.setOnClickListener {
            TACPP(requireActivity()).launch(0)
        }

        binding.feedback.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",
                            EMAIL, null
                        )
                    ), "Send email...")
            )
        }

        binding.about.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(), AboutActivity::class.java
                )
            )
        }

        //
        checkColor()
        initColorClick()
        //
        checkArabic()
        initArabicClick()
        //
        checkFontSeekBar()
        initFontSeekBarSeek()
        //
        checkTransliteration()
        initTransliterationClick()

        Handler(Looper.getMainLooper()).postDelayed({
            activity?.run {
                binding.nScroll.scrollBy(0, intent.getIntExtra("SCROLL",0))
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    binding.tSwitchLay.visibility = View.VISIBLE
                    binding.tSwitchView.visibility = View.VISIBLE
                } else {
                    binding.tSwitchLay.visibility = View.GONE
                    binding.tSwitchView.visibility = View.GONE
                }
            }
        },10)

        return _binding?.root
    }

    override fun onPause() {
        super.onPause()
        SystemData(requireContext()).settingsScroll = binding.nScroll.scrollY
    }

    private fun initViewItemClip() {
        binding.openFont.clipToOutline = true
        binding.green.clipToOutline = true
        binding.blue.clipToOutline = true
        binding.orange.clipToOutline = true
    }

    private fun checkColor() {
        binding.greenCheck.visibility = View.GONE
        binding.blueCheck.visibility = View.GONE
        binding.orangeCheck.visibility = View.GONE
        when (applicationData.primaryColor) {
            Application.GREEN -> binding.greenCheck.visibility = View.VISIBLE
            Application.BLUE -> binding.blueCheck.visibility = View.VISIBLE
            Application.ORANGE -> binding.orangeCheck.visibility = View.VISIBLE
        }
    }

    private fun initColorClick() {
        binding.green.setOnClickListener {
            applicationData.primaryColor = Application.GREEN
            reCreate()
        }
        binding.blue.setOnClickListener {
            applicationData.primaryColor = Application.BLUE
            reCreate()
        }
        binding.orange.setOnClickListener {
            applicationData.primaryColor = Application.ORANGE
            reCreate()
        }
    }

    private fun checkArabic() {
        binding.arabicGroup.check(
            if (applicationData.arabic)
                R.id.uthmani else R.id.indoPk
        )
    }

    private fun initArabicClick() {
        binding.arabicGroup.setOnCheckedChangeListener { _, checkedId ->
            applicationData.arabic = checkedId != R.id.indoPk
//            if (applicationData.arabic) {
//                binding.tajweed.expand(true)
//                binding.tajweedSwi.isChecked = applicationData.tajweed
//            }
//            else {
//                binding.tajweed.collapse(true)
//                applicationData.tajweed = false
//            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkFontSeekBar() {
        val arabic = applicationData.arabicFontSize.toInt()
        binding.arabicSizeText.text = "${arabic}sp"
        binding.arabicTranslationSeek.progress = (arabic-16)/2

        val transliteration = applicationData.transliterationFontSize.toInt()
        binding.transliterationSizeText.text = "${transliteration}sp"
        binding.transliterationFontSeek.progress = (transliteration-16)/2

        val translation = applicationData.translationFontSize.toInt()
        binding.translationSizeText.text = "${translation}sp"
        binding.translationSeek.progress = (translation-16)/2
    }

    private fun initFontSeekBarSeek() {
        binding.arabicTranslationSeek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean) {
                    val size = ((progress*2)+16).toFloat()
                    applicationData.arabicFontSize = size
                    binding.arabicSizeText.text = "$size"
                    binding.arabicSizeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.e("onStartTrackingTouch", "onStartTrackingTouch")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.e("onStopTrackingTouch", "onStopTrackingTouch")
                }

            }
        )

        binding.transliterationFontSeek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean) {
                    val size = ((progress*2)+16).toFloat()
                    applicationData.transliterationFontSize = size
                    binding.transliterationSizeText.text = "$size"
                    binding.transliterationFontText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.e("Start", "Tracking")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.e("Stop", "Not Tracking")
                }

            }
        )

        binding.translationSeek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean) {
                    val size = ((progress*2)+16).toFloat()
                    applicationData.translationFontSize = size
                    binding.translationSizeText.text = "$size"
                    binding.translationSizeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.e("Start", "Tracking")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.e("Stop", "Not Tracking")
                }

            }
        )
    }

    private fun checkTransliteration() {
        binding.transliterationSwitch.isChecked = applicationData.transliteration
    }

    private fun initTransliterationClick() {
        binding.transliterationSwitch.setOnCheckedChangeListener { _, isChecked ->
            applicationData.transliteration = isChecked
        }
    }

    private fun reCreate() {
        coroutine.launch {
            delay(250)
            val temp = ViewModelProvider(requireActivity(),
                HomeViewModelFactory()
            )[HomeViewModel::class.java]
            val temp2 = ViewModelProvider(requireActivity(),
                LearnViewModelFactory()
            )[LearnViewModel::class.java]
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                ).putExtra("RECREATE", true)
                    .putExtra("HOME_DATA", temp.homeData.value)
                    .putExtra("DATE", temp.date.value)
                    .putExtra("SCROLL", binding.nScroll.scrollY)
                    .putExtra("POST_ID", temp.postId.value)
                    .putExtra("LOADED",
                        when (temp.locationLoaded.value) {
                            true -> 1
                            else -> 0
                        }
                    ).putExtra("CITY", temp.locationName.value?.city)
                    .putExtra("COUNTRY", temp.locationName.value?.country)
                    .putExtra("LATITUDE", temp.location.value?.latitude)
                    .putExtra("LONGITUDE", temp.location.value?.longitude)
                    .putExtra("ANIMATION", temp.noAnimation.value)
                    .putExtra("VIDEO_DATA", temp2.data.value)
            )
            Objects.requireNonNull(
                requireActivity().overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            )
            requireActivity().finish()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (applicationData.arabic) {
//            binding.tajweed.expand(false)
//            binding.tajweedSwi.isChecked = applicationData.tajweed
//        } else {
//            binding.tajweed.collapse(false)
//            binding.tajweedSwi.isChecked = applicationData.tajweed
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}