package com.code.apppraytime.screen.layout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.R
import com.code.apppraytime.compass.GPSTracker
import com.code.apppraytime.constant.Dua
import com.code.apppraytime.databinding.FragmentSalatBinding
import com.code.apppraytime.factory.HomeViewModelFactory
import com.code.apppraytime.model.*
import com.code.apppraytime.repository.SalatRepository.salatNames
import com.code.apppraytime.shared.SalatTimes
import com.code.apppraytime.theme.ApplicationTheme
import com.code.apppraytime.times.Coordinates
import com.code.apppraytime.times.Madhab
import com.code.apppraytime.times.PrayerTimes
import com.code.apppraytime.times.data.DateComponents
import com.code.apppraytime.viewModel.HomeViewModel
import com.code.apppraytime.views.calander.CalendarView
import com.code.apppraytime.views.calander.Tools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import com.code.apppraytime.views.sun.model.Time
import java.text.ParsePosition
import java.text.SimpleDateFormat

class Salat : AppCompatActivity() {

    companion object {
        const val TRUE = true
        const val FALSE = false
        const val DATE = "DATE"
        const val CITY = "CITY"
        const val LOADED = "LOADED"
        const val COUNTRY = "COUNTRY"
        const val HOME_DATA = "HOME_DATA"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val ANIMATION = "ANIMATION"
        const val POST_ID = "POST_ID"
        const val datePattern = "dd-MM-yyyy"
    }

    /*private var _binding: FragmentSalatBinding? = null
    private val binding get() = _binding!!*/

    private lateinit var binding: FragmentSalatBinding

    private val viewModel by lazy {
        ViewModelProvider(this,
            HomeViewModelFactory()
        )[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = FragmentSalatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        saveDataToViewModel()

        viewModel.locationName.observe(this) {
            binding.locationText.text = "${it.city}, ${it.country}"
        }

        viewModel.location.observe(this) {
            calculateSalatTime(it.latitude, it.longitude)
        }

        CoroutineScope(Dispatchers.Default).launch { setUpCalendar() }

        binding.university.text = SalatTimes(this).getCalculationText()

        setSalatTime()
    }

/*
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentSalatBinding.inflate(inflater, container, false)
        saveDataToViewModel()

        viewModel.locationName.observe(viewLifecycleOwner) {
            binding.locationText.text = "${it.city}, ${it.country}"
        }

        viewModel.location.observe(viewLifecycleOwner) {
            calculateSalatTime(it.latitude, it.longitude)
        }

        CoroutineScope(Dispatchers.Default).launch { setUpCalendar() }

        binding.university.text = SalatTimes(requireContext()).getCalculationText()

        setSalatTime()

        return _binding?.root
    }
*/

    private fun saveDataToViewModel() {
        val temp = intent.getParcelableArrayListExtra<HomeModel>(HOME_DATA)
        if (temp != null) {
            viewModel.addValue(temp)
            intent.run {
                viewModel.postId.value = getStringExtra(POST_ID)
                viewModel.date.value = getStringExtra(DATE)
                viewModel.noAnimation.value = getBooleanExtra(ANIMATION, false)
                viewModel.locationName.value = LocModelName(
                    getStringExtra(CITY)?:"",
                    getStringExtra(COUNTRY)?:""

                )
                viewModel.location.value = LocModel(
                    getDoubleExtra(LATITUDE, 0.0),
                    getDoubleExtra(LONGITUDE, 0.0)
                )
                getIntExtra(LOADED, 0).let {
                    when(it) {
                        1 -> viewModel.locationLoaded.value = true
                        else -> viewModel.locationLoaded.value = null
                    }
                }
                removeExtra(DATE)
                removeExtra(CITY)
                removeExtra(LOADED)
                removeExtra(COUNTRY)
                removeExtra(HOME_DATA)
                removeExtra(LATITUDE)
                removeExtra(LONGITUDE)
                removeExtra(ANIMATION)
            }
        }
    }

    private fun calculateSalatTime(lat: Double, lon: Double) {
        val date = DateComponents.from(Date())
        val coordinates = Coordinates(lat, lon)
        val params = SalatTimes(this).getCalculation()
        params.madhab = if (SalatTimes(this).hanafi)
            Madhab.HANAFI else Madhab.SHAFI
        val prayerTimes = PrayerTimes(coordinates, date, params)

        val timeList = ArrayList<String>().apply {
            add(
                SimpleDateFormat(Home.timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.fajr!!.time))
            )
            add(
                SimpleDateFormat(Home.timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.dhuhr!!.time))
            )
            add(
                SimpleDateFormat(Home.timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.asr!!.time))
            )
            add(
                SimpleDateFormat(Home.timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.maghrib!!.time))
            )
            add(
                SimpleDateFormat(Home.timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.isha!!.time))
            )
        }

        runOnUiThread {
            viewModel.salatTime.value = calculate(
                if (salatNames.contains(prayerTimes.currentPrayer().name))
                    prayerTimes.currentPrayer().name else prayerTimes.nextPrayer().name,
                prayerTimes
            )

            viewModel.salatTimeList.value = timeList
            viewModel.sunData.value = Sun(
                prayerTimes.maghrib!!.time,
                prayerTimes.sunrise!!.time
            )
        }
    }

    private fun calculate(name: String, prayerTimes: PrayerTimes): SalatModel {
        var salatName = ""
        var salatTime = ""
        when (name) {
            salatNames[0] -> {
                salatName = "Salat Fajar"
                salatTime = SimpleDateFormat(Home.timePattern, Locale.US)
                    .format(Date(prayerTimes.fajr!!.time))
            }
            salatNames[1] -> {
                salatName = "Salat Duhr"
                salatTime = SimpleDateFormat(Home.timePattern, Locale.US)
                    .format(Date(prayerTimes.dhuhr!!.time))
            }
            salatNames[2] -> {
                salatName = "Salat Asr"
                salatTime = SimpleDateFormat(Home.timePattern, Locale.US)
                    .format(Date(prayerTimes.asr!!.time))
            }
            salatNames[3] -> {
                salatName = "Salat Maghrib"
                salatTime = SimpleDateFormat(Home.timePattern, Locale.US)
                    .format(Date(prayerTimes.maghrib!!.time))
            }
            salatNames[4] -> {
                salatName = "Salat Isha"
                salatTime = SimpleDateFormat(Home.timePattern, Locale.US)
                    .format(Date(prayerTimes.isha!!.time))
            }
        }

        return SalatModel(salatTime, salatName)
    }

    private fun setUpCalendar() {
        val startTime: Calendar = Calendar.getInstance()
        startTime.add(Calendar.MONTH, -3)

        val endTime: Calendar = Calendar.getInstance()
        endTime.add(Calendar.MONTH, 3)

        val datesToBeColored = ArrayList<String?>()
        datesToBeColored.add(Tools.formattedDateToday)

        binding.calander.setUpCalendar(startTime.timeInMillis,
            endTime.timeInMillis,
            datesToBeColored,
            object : CalendarView.OnCalendarListener {
                override fun onDateSelected(date: String?) {
                    val d = DateComponents.from(stringToDate(date))
                    if (date != SimpleDateFormat("dd-MM-yyyy",
                            Locale.getDefault()).format(Date()).toString())
                                setSalatTime(d)
                    else setSalatTime()
                    datesToBeColored.clear()
                    datesToBeColored.add(date)
                    binding.calander.update(
                        startTime.timeInMillis, endTime.timeInMillis, datesToBeColored
                    )
                }
            }
        )
    }

    private fun setSalatTime(date: DateComponents) {
        binding.fajr.setActive(FALSE, TRUE)
        binding.duhr.setActive(FALSE, TRUE)
        binding.asr.setActive(FALSE, TRUE)
        binding.magrib.setActive(FALSE, TRUE)
        binding.isha.setActive(FALSE, TRUE)

        binding.fajr.setSubtitle("")
        binding.fajr.setTitleTextAppearance(R.style.titleMid)
        binding.fajr.setAnchorTextAppearance(R.style.titleMid)

        binding.duhr.setSubtitle("")
        binding.duhr.setTitleTextAppearance(R.style.titleMid)
        binding.duhr.setAnchorTextAppearance(R.style.titleMid)

        binding.asr.setSubtitle("")
        binding.asr.setTitleTextAppearance(R.style.titleMid)
        binding.asr.setAnchorTextAppearance(R.style.titleMid)

        binding.magrib.setSubtitle("")
        binding.magrib.setTitleTextAppearance(R.style.titleMid)
        binding.magrib.setAnchorTextAppearance(R.style.titleMid)

        binding.isha.setSubtitle("")
        binding.isha.setTitleTextAppearance(R.style.titleMid)
        binding.isha.setAnchorTextAppearance(R.style.titleMid)

        val gpsTracker = GPSTracker(this)
        val coordinates = Coordinates(gpsTracker.latitude, gpsTracker.longitude)
        val params = SalatTimes(this).getCalculation()
        params.madhab = if (SalatTimes(this).hanafi)
            Madhab.HANAFI else Madhab.SHAFI
        val prayerTimes = PrayerTimes(coordinates, date, params)

        binding.fajr.setAnchor(SimpleDateFormat(Home.timePattern, Locale.getDefault())
            .format(Date(prayerTimes.fajr!!.time)))
        binding.duhr.setAnchor(SimpleDateFormat(Home.timePattern, Locale.getDefault())
            .format(Date(prayerTimes.dhuhr!!.time)))
        binding.asr.setAnchor(SimpleDateFormat(Home.timePattern, Locale.getDefault())
            .format(Date(prayerTimes.asr!!.time)))
        binding.magrib.setAnchor(SimpleDateFormat(Home.timePattern, Locale.getDefault())
            .format(Date(prayerTimes.maghrib!!.time)))
        binding.isha.setAnchor(SimpleDateFormat(Home.timePattern, Locale.getDefault())
            .format(Date(prayerTimes.isha!!.time)))
    }

    private fun setSalatTime() {
        viewModel.salatTime.observe(this@Salat) {
            val noAnim = viewModel.noAnimation.value ?: false
            binding.fajr.setActive(false, noAnim)
            binding.duhr.setActive(false, noAnim)
            binding.asr.setActive(false, noAnim)
            binding.magrib.setActive(false, noAnim)
            binding.isha.setActive(false, noAnim)
            when (it.salat) {
                "Salat Fajar" -> {
                    binding.fajr.setActive(true, noAnim)
                    binding.fajr.setSubtitle(Dua.fajar)
                    binding.fajr.setTitleTextAppearance(R.style.titleMax)
                    binding.fajr.setSubtitleTextAppearance(R.style.titleMin)
                    binding.fajr.setAnchorTextAppearance(R.style.titleMax)
                }
                "Salat Duhr" -> {
                    binding.duhr.setActive(true, noAnim)
                    binding.duhr.setSubtitle(Dua.duhur)
                    binding.duhr.setTitleTextAppearance(R.style.titleMax)
                    binding.duhr.setSubtitleTextAppearance(R.style.titleMin)
                    binding.duhr.setAnchorTextAppearance(R.style.titleMax)
                }
                "Salat Asr" -> {
                    binding.asr.setActive(true, noAnim)
                    binding.asr.setSubtitle(Dua.asr)
                    binding.asr.setTitleTextAppearance(R.style.titleMax)
                    binding.asr.setSubtitleTextAppearance(R.style.titleMin)
                    binding.asr.setAnchorTextAppearance(R.style.titleMax)
                }
                "Salat Maghrib" -> {
                    binding.magrib.setActive(true, noAnim)
                    binding.magrib.setSubtitle(Dua.maghrib)
                    binding.magrib.setTitleTextAppearance(R.style.titleMax)
                    binding.magrib.setSubtitleTextAppearance(R.style.titleMin)
                    binding.magrib.setAnchorTextAppearance(R.style.titleMax)
                }
                "Salat Isha" -> {
                    binding.isha.setActive(true, noAnim)
                    binding.isha.setSubtitle(Dua.isha)
                    binding.isha.setTitleTextAppearance(R.style.titleMax)
                    binding.isha.setSubtitleTextAppearance(R.style.titleMin)
                    binding.isha.setAnchorTextAppearance(R.style.titleMax)
                }
            }
        }

        viewModel.salatTimeList.observe(this) {
            binding.fajr.setAnchor(it[0])
            binding.duhr.setAnchor(it[1])
            binding.asr.setAnchor(it[2])
            binding.magrib.setAnchor(it[3])
            binding.isha.setAnchor(it[4])
        }

        viewModel.sunData.observe(this) {
            binding.set.text =
                SimpleDateFormat(Home.timePattern, Locale.getDefault()).format(Date(it.sunset))
            binding.rise.text =
                SimpleDateFormat(Home.timePattern, Locale.getDefault()).format(Date(it.sunrise))
            binding.ssv.sunriseTime = Time(
                SimpleDateFormat("HH", Locale.getDefault()).format(it.sunrise).toString().toInt(),
                SimpleDateFormat("mm", Locale.getDefault()).format(Date(it.sunrise)).toString()
                    .toInt()
            )
            binding.ssv.sunsetTime = Time(
                SimpleDateFormat("HH", Locale.getDefault()).format(it.sunset).toString().toInt(),
                SimpleDateFormat("mm", Locale.getDefault()).format(Date(it.sunset)).toString()
                    .toInt()
            )
            if (viewModel.noAnimation.value == true)
                binding.ssv.startWithoutAnimate()
            else {
                binding.ssv.startAnimate()
                viewModel.noAnimation.value = true
            }
        }
    }

    private fun stringToDate(aDate: String?): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        return SimpleDateFormat(
            datePattern, Locale.getDefault()
        ).parse(aDate, pos)
    }

    /*override fun onDetach() {
        super.onDetach()
        _binding = null
    }*/
}