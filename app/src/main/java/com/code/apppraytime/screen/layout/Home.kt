package com.code.apppraytime.screen.layout

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.code.apppraytime.compass.Location
import com.code.apppraytime.databinding.FragmentHomeBinding
import com.code.apppraytime.factory.HomeViewModelFactory
import com.code.apppraytime.http.DateLoader
import com.code.apppraytime.interfaces.LocationInterface
import com.code.apppraytime.model.*
import com.code.apppraytime.times.Coordinates
import com.code.apppraytime.times.Madhab
import com.code.apppraytime.times.PrayerTimes
import com.code.apppraytime.times.data.DateComponents
import com.code.apppraytime.repository.SalatRepository.salatNames
import com.code.apppraytime.screen.*
import com.code.apppraytime.shared.Application
import com.code.apppraytime.shared.SalatTimes
import com.code.apppraytime.shared.SavedLocation
import com.code.apppraytime.ui.HeaderImage
import com.code.apppraytime.viewModel.HomeViewModel
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.constants.ProviderType
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Home : Fragment(), LocationInterface, /*HomeInterface,*/ /*LoadCallbackHome,*/ LocationListener {

    companion object {
        const val timePattern = "hh:mm a"
        const val datePattern = "dd-MM-yyyy"
    }

    private var processing = false
    private var locationManager: LocationManager? = null

    private val awesomeConfiguration by lazy {
        LocationConfiguration.Builder()
            .keepTracking(true)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Location Permission Required !")
                    .requiredPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    ).build()
            ).useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .requiredTimeInterval((3 * 60 * 1000).toLong())
                    .requiredDistanceInterval(0)
                    .acceptableAccuracy(5.0f)
                    .acceptableTimePeriod((3 * 60 * 1000).toLong())
                    .gpsMessage("Turn on GPS?")
                    .setWaitPeriod(ProviderType.GPS, (20 * 1000).toLong())
                    .setWaitPeriod(ProviderType.NETWORK, (20 * 1000).toLong())
                    .build()
            ).build()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    /*private val homeLoader by lazy {
        activity?.let { HomeLoader(it, this) }
    }*/

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(),
            HomeViewModelFactory()
        )[HomeViewModel::class.java]
    }

    /*private val homeAdapter by lazy {
        HomeAdapter(requireContext(),
            viewModel.homeData.value,
            this
        )
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        saveDataToViewModel()

        /*binding.homeRecycler.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeAdapter
        }*/

        initObserves()
        initDefaultViews()
//        if (binding.locationText.text.toString().isEmpty())
        if (binding.location.text.toString().isEmpty())
            initLocationProcess()
        initViewModelProcess()

        return _binding?.root
    }

    private fun saveDataToViewModel() {
        val temp = requireActivity().intent.getParcelableArrayListExtra<HomeModel>("HOME_DATA")
        if (temp != null) {
            viewModel.addValue(temp)
            requireActivity().intent.run {
                viewModel.postId.value = getStringExtra("POST_ID")
                viewModel.date.value = getStringExtra("DATE")
                viewModel.noAnimation.value = getBooleanExtra("ANIMATION", false)
                viewModel.locationName.value = LocModelName(
                    getStringExtra("CITY")?:"",
                    getStringExtra("COUNTRY")?:""

                )
                viewModel.location.value = LocModel(
                    getDoubleExtra("LATITUDE", 0.0),
                    getDoubleExtra("LONGITUDE", 0.0)
                )
                getIntExtra("LOADED", 0).let {
                    when(it) {
                        1 -> viewModel.locationLoaded.value = true
                        else -> viewModel.locationLoaded.value = null
                    }
                }
                removeExtra("DATE")
                removeExtra("CITY")
                removeExtra("LOADED")
                removeExtra("COUNTRY")
                removeExtra("HOME_DATA")
                removeExtra("LATITUDE")
                removeExtra("LONGITUDE")
                removeExtra("ANIMATION")
            }
        }
    }

    private fun initLocationProcess() {
        (locationManager ?:  LocationManager.Builder(requireActivity().applicationContext)
            .activity(activity)
            .configuration(
                LocationConfiguration.Builder()
                    .keepTracking(false)
                    .askForPermission(
                        PermissionConfiguration.Builder()
                            .rationaleMessage("Location Permission Required !")
                            .requiredPermissions(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION))
                            .build()
                    ).useGooglePlayServices(
                        GooglePlayServicesConfiguration.Builder()
                            .fallbackToDefault(true)
                            .askForGooglePlayServices(false)
                            .askForSettingsApi(true)
                            .failOnSettingsApiSuspended(false)
                            .ignoreLastKnowLocation(false)
                            .setWaitPeriod(20 * 1000)
                            .build()
                    ).build()
            ).notify(this).build()).get()

        if (viewModel.locationLoaded.value != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                activity?.let {
                    binding.locLogo.visibility = View.GONE
                    binding.locProg.visibility = View.VISIBLE
                    locationManager = LocationManager.Builder(it.applicationContext)
                        .activity(it)
                        .configuration(awesomeConfiguration)
                        .notify(this)
                        .build()
                    locationManager?.get()
                }
            }, 250)
        }
    }

    private fun initObserves() {
        viewModel.date.observe(viewLifecycleOwner) {
            binding.islamicDate.text = it.toString()
        }

        viewModel.locationName.observe(viewLifecycleOwner) {
            binding.location.text = "${it.country}, ${it.city}"
//            binding.locationText.text = it.city
        }

        viewModel.location.observe(viewLifecycleOwner) {
            calculateSalatTime(it.latitude, it.longitude)
        }

        /*viewModel.homeData.observe(viewLifecycleOwner) {
            homeAdapter.swap(it)
        }*/

        viewModel.salatTime.observe(viewLifecycleOwner) {
            binding.salatName.text = it.salat
            binding.salatTime.text = it.time
        }

        viewModel.refresh.observe(viewLifecycleOwner) {
            if (it == 0) {
                /*binding.homeRecycler.scrollToPosition(0)*/
                binding.appBar.setExpanded(true, true)
            }
        }
    }

    private fun initViewModelProcess() {
        if (viewModel.date.value == null)
            CoroutineScope(Dispatchers.IO).launch {
                DateLoader(requireActivity(), viewModel).calculateDate()
            }

        if (viewModel.homeData.value == null)
            viewModel.addValue(ArrayList<HomeModel?>().also { it.add(null) })
    }

    private fun calculateSalatTime(lat: Double, lon: Double) {
        val date = DateComponents.from(Date())
        val coordinates = Coordinates(lat, lon)
        val params = SalatTimes(requireContext()).getCalculation()
        params.madhab = if (SalatTimes(requireContext()).hanafi)
            Madhab.HANAFI else Madhab.SHAFI
        val prayerTimes = PrayerTimes(coordinates, date, params)

        val timeList = ArrayList<String>().apply {
            add(
                SimpleDateFormat(timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.fajr!!.time))
            )
            add(
                SimpleDateFormat(timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.dhuhr!!.time))
            )
            add(
                SimpleDateFormat(timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.asr!!.time))
            )
            add(
                SimpleDateFormat(timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.maghrib!!.time))
            )
            add(
                SimpleDateFormat(timePattern, Locale.getDefault())
                    .format(Date(prayerTimes.isha!!.time))
            )
        }

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

    fun calculate(name: String, prayerTimes: PrayerTimes): SalatModel {
        var salatName = ""
        var salatTime = ""
        when (name) {
            salatNames[0] -> {
                salatName = "Salat Fajar"
                salatTime = SimpleDateFormat(timePattern, Locale.US)
                    .format(Date(prayerTimes.fajr!!.time))
            }
            salatNames[1] -> {
                salatName = "Salat Duhr"
                salatTime = SimpleDateFormat(timePattern, Locale.US)
                    .format(Date(prayerTimes.dhuhr!!.time))
            }
            salatNames[2] -> {
                salatName = "Salat Asr"
                salatTime = SimpleDateFormat(timePattern, Locale.US)
                    .format(Date(prayerTimes.asr!!.time))
            }
            salatNames[3] -> {
                salatName = "Salat Maghrib"
                salatTime = SimpleDateFormat(timePattern, Locale.US)
                    .format(Date(prayerTimes.maghrib!!.time))
            }
            salatNames[4] -> {
                salatName = "Salat Isha"
                salatTime = SimpleDateFormat(timePattern, Locale.US)
                    .format(Date(prayerTimes.isha!!.time))
            }
        }

        return SalatModel(salatTime, salatName)
    }

    private fun initDefaultViews() {
        /*binding.dua.clipToOutline = true
        binding.qibla.clipToOutline = true
        binding.quran.clipToOutline = true
        binding.hadith.clipToOutline = true
        binding.mosque.clipToOutline = true*/
        activity?.run {
            if (Application(this).vectorIcon)
                HeaderImage(this).loadHeaderVector(binding)
            else HeaderImage(this).loadHeaderImages(binding)

            binding.cardViewQiblaCompass.setOnClickListener {
                startActivity(
                    Intent(this, QiblaActivity::class.java)
                )
            }
            binding.ivQiblaCompass.setOnClickListener {
                startActivity(
                    Intent(this, QiblaActivity::class.java)
                )
            }

            binding.cardViewMosque.setOnClickListener {
                startActivity(
                    Intent(this, MosqueActivity::class.java)
                )
            }
            binding.ivMosque.setOnClickListener {
                startActivity(
                    Intent(this, MosqueActivity::class.java)
                )
            }

            binding.cardViewTasbih.setOnClickListener {
//                viewModel.homeData.value?.let {
//                    val data = ArrayList<HomeModel?>()
//                    data.addAll(it.shuffled())
//                    viewModel.homeData.value = data
//                }
//                HadithRootActivity.launch(requireContext())
                startActivity(
                    Intent(
                        requireContext(),
                        TasbihActivity::class.java
                    )
                )
            }
            binding.ivTasbih.setOnClickListener {
//                viewModel.homeData.value?.let {
//                    val data = ArrayList<HomeModel?>()
//                    data.addAll(it.shuffled())
//                    viewModel.homeData.value = data
//                }
//                HadithRootActivity.launch(requireContext())
                startActivity(
                    Intent(
                        requireContext(),
                        TasbihActivity::class.java
                    )
                )
            }

            binding.cardViewSalat.setOnClickListener {
                startActivity(
                    Intent(this, Salat::class.java)
                )
            }
            binding.ivSalat.setOnClickListener {
                startActivity(
                    Intent(this, Salat::class.java)
                )
            }

            binding.cardViewQuran.setOnClickListener {
                startActivity(
                    Intent(this, QuranActivity::class.java)
                )
            }
            binding.ivQuran.setOnClickListener {
                startActivity(
                    Intent(this, QuranActivity::class.java)
                )
            }
        }
    }

    /*override fun onBottom(id: String) {
        if (!processing) {
            processing = true
            activity?.let {  homeLoader?.read(viewModel.postId.value) }
        }
    }*/

    /*override fun onEmpty() {
        if ((viewModel.homeData.value?.size?:0)>0 &&
            viewModel.homeData.value!![viewModel.homeData.value!!.size-1] == null) {
            viewModel.removeValue(viewModel.homeData.value!!.size - 1)
            viewModel.addValue(
                ArrayList<HomeModel?>().also {
                    it.add(HomeModel(type = Type.FINISHED))
                }
            )
        }
        //Toast.makeText(requireContext(), "End of data", Toast.LENGTH_SHORT).show()
    }

    override fun onError(e: String) {
        Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show()
    }

    override fun onLoaded(data: ArrayList<HomeModel?>) {
        processing = false
        if ((viewModel.homeData.value?.size?:0)>0)
            viewModel.removeValue(viewModel.homeData.value!!.size-1)
        viewModel.addValue(data)
        viewModel.addValue(ArrayList<HomeModel?>().also { it.add(null) })
        viewModel.postId.value = data[data.size-1]?.id.toString()
    }*/

    override fun onProcessTypeChanged(processType: Int) {
        Log.e("onProcessTypeChanged", processType.toString())
    }

    override fun onLocationChanged(location: android.location.Location?) {
        location?.let {
            _binding?.run {
                locLogo.visibility = View.VISIBLE
                locProg.visibility = View.GONE
                viewModel.location.value = LocModel(it.latitude, it.longitude)
                CoroutineScope(Dispatchers.Default).launch {
                    SavedLocation(requireContext()).run {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                    Location(
                        requireContext(), this@Home
                    ).getCityName(it.latitude, it.longitude)
                }
                viewModel.locationLoaded.value = viewModel.locationLoaded.value != null
            }

        }
    }

    override fun onLocationFailed(type: Int) {
        Log.e("onLocationFailed", type.toString())
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean) {
        Log.e("onPermissionGranted", alreadyHadPermission.toString())
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e("onStatusChanged", provider.toString())
    }

    override fun onProviderEnabled(provider: String?) {
        Log.e("onProviderEnabled", provider.toString())
    }

    override fun onProviderDisabled(provider: String?) {
        Log.e("onProviderDisabled", provider.toString())
    }

    override suspend fun located(city: String, country: String) {
        SavedLocation(requireContext()).let {
            it.city = city
            it.country = country
        }
        activity?.runOnUiThread {
            if (viewModel.locationName.value?.city != city) {
                viewModel.locationName.value = LocModelName(
                    city, country
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.cancel()
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}