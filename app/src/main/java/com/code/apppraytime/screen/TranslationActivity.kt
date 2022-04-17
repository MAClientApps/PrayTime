package com.code.apppraytime.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.code.apppraytime.R
import com.code.apppraytime.databinding.ActivityTranslationBinding
import com.code.apppraytime.shared.Application
import com.code.apppraytime.shared.Application.Companion.BN_BAYAAN
import com.code.apppraytime.shared.Application.Companion.BN_FOZLUR
import com.code.apppraytime.shared.Application.Companion.BN_MUJIBUR
import com.code.apppraytime.shared.Application.Companion.BN_TAISIRUL
import com.code.apppraytime.shared.Application.Companion.EN_HALEEM
import com.code.apppraytime.shared.Application.Companion.EN_SAHIH
import com.code.apppraytime.shared.Application.Companion.HINDI_FAROOQ
import com.code.apppraytime.shared.Application.Companion.IN_BAHASA
import com.code.apppraytime.shared.Application.Companion.TR_DIYANET
import com.code.apppraytime.shared.Application.Companion.UR_JUNAGARHI
import com.code.apppraytime.theme.ApplicationTheme

class TranslationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        initTextViews()

        when(Application(this).translation) {
            BN_BAYAAN -> binding.translationGroup.check(R.id.bn_bayaan)
            BN_FOZLUR -> binding.translationGroup.check(R.id.bn_fozlur)
            BN_MUJIBUR -> binding.translationGroup.check(R.id.bn_mujibur)
            BN_TAISIRUL -> binding.translationGroup.check(R.id.bn_taisirul)
            EN_HALEEM -> binding.translationGroup.check(R.id.en_haleem)
            EN_SAHIH -> binding.translationGroup.check(R.id.en_sahih)
            HINDI_FAROOQ -> binding.translationGroup.check(R.id.hindi_farooq)
            IN_BAHASA -> binding.translationGroup.check(R.id.in_bahasa)
            TR_DIYANET -> binding.translationGroup.check(R.id.tr_diyanet)
            UR_JUNAGARHI -> binding.translationGroup.check(R.id.ur_junagarhi)
        }

        binding.translationGroup.setOnCheckedChangeListener { _, checkedId ->
            Application(this).translation =
                when(checkedId) {
                    R.id.bn_bayaan -> BN_BAYAAN
                    R.id.bn_fozlur -> BN_FOZLUR
                    R.id.bn_mujibur -> BN_MUJIBUR
                    R.id.bn_taisirul -> BN_TAISIRUL
                    R.id.en_haleem -> EN_HALEEM
                    R.id.en_sahih -> EN_SAHIH
                    R.id.hindi_farooq -> HINDI_FAROOQ
                    R.id.in_bahasa -> IN_BAHASA
                    R.id.tr_diyanet -> TR_DIYANET
                    R.id.ur_junagarhi -> UR_JUNAGARHI
                    else -> EN_SAHIH
                }
        }
    }

    private fun initTextViews() {
        binding.bnBayaan.text = convertText(
            "Bengali", "Bayaan Foundation"
        )
        binding.bnFozlur.text = convertText(
            "Bengali", "Fozlur Rahman"
        )
        binding.bnMujibur.text = convertText(
            "Bengali", "Mujibur Rahman"
        )
        binding.bnTaisirul.text = convertText(
            "Bengali", "Taisirul Quran"
        )
        binding.enHaleem.text = convertText(
            "English", "Abdul Haleem"
        )
        binding.enSahih.text = convertText(
            "English", "Sahih International"
        )
        binding.hindiFarooq.text = convertText(
            "Hindi", "Farooq/Nadwi"
        )
        binding.inBahasa.text = convertText(
            "Indonesian", "Bahasa"
        )
        binding.trDiyanet.text = convertText(
            "Turkish", "Diyanet Isleri"
        )
        binding.urJunagarhi.text = convertText(
            "Urdu", "Muhammad Junagarhi"
        )
    }

    private fun convertText(first: String, second: String): Spanned {
        val accentColor = ApplicationTheme(this).getTextColor()
        return HtmlCompat.fromHtml(
            "<span style=\"color:$accentColor\"><b>$first</b></span><br>$second",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}