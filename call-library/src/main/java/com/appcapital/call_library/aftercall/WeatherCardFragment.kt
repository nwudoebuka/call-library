
package com.appcapital.call_library.aftercall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcapital.call_library.R
import com.appcapital.call_library.aftercall.adapter.NewsAdapter
import com.appcapital.call_library.databinding.FragmentWeatherCardBinding
import com.appcapital.call_library.utils.SharedPreferencesHelper
import com.appcapital.call_library.viewmodel.AfterCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.appcapital.call_library.api.Result
import com.appcapital.call_library.model.CurrentConditions
import com.appcapital.call_library.model.NewsArticle
import com.appcapital.call_library.model.NewsResponse
import com.appcapital.call_library.model.WeatherResponse
import com.appcapital.call_library.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class WeatherCardFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = WeatherCardFragment::class.simpleName.toString()
    private lateinit var afterCallViewModel: AfterCallViewModel
    private var _binding: FragmentWeatherCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var appCustomView: View
    private var weatherData: List<CurrentConditions>? = null
    private var userLocation: Pair<String?, String?>? = null
    private lateinit var newsAdapter: NewsAdapter
    private var countryCode = "us"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afterCallViewModel = ViewModelProvider(this).get(AfterCallViewModel::class.java)
        arguments?.let {
            param1 = it.getString(param1)
            param2 = it.getString(param2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherCardBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        val rootView = view
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // Find the app_view by ID in the inflated view
//        val appView = rootView.findViewById<FrameLayout>(R.id.app_view)
        val appConfig = SharedPreferencesHelper.getAppConfig(requireActivity())
        // Create a new TextView
//        val textView = TextView(context).apply {
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER
//            }
//            text = "Hello, World!"
//            textSize = 20f
//        }
//
//        // Add the TextView to the app_view
        appCustomView = inflater.inflate(appConfig.customView, null)
       // appView?.addView(appCustomView)
//        newsAdapter = NewsAdapter(
//            requireContext(),
//            emptyList(),  // Initially empty news list
//            null,         // Initially no weather data
//            appCustomView,
//            null
//        )
        initObserver()
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                400
            )
        } else {
           getLastLocation()
        }
        return rootView
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    userLocation = Utils.getCityAndCountry( requireActivity(), latitude, longitude)
                    Log.d(TAG,"userLoc: $userLocation")
                    if(userLocation != null){
                        val countryCode = userLocation?.second?.let { it1 -> Utils.getCountryCode(it1) }
                        if(countryCode != null){
                            this.countryCode = countryCode
//                            getNews(countryCode)
                        }
                        userLocation?.first?.let { it1 -> afterCallViewModel.fetchWeather(it1, Utils.getCurrentDate(), Utils.getCurrentDate(), "2396C5X9NKU6E6RC9GAUUJB5Q") }

                    }
//                    userLocation.second?.let {
//                        val countryCode = Utils.getCountryCode(it)
//                        if(countryCode != null){
//                         getNews(countryCode)
//                        }
//                    }
//                    userLocation.first?.let {
//                        afterCallViewModel.fetchWeather(it, Utils.getCurrentDate(), Utils.getCurrentDate(), "2396C5X9NKU6E6RC9GAUUJB5Q")
//                    }
                } ?: run {
                    requestNewLocationData()
                }
            }
    }

    private fun getNews(countryCode: String){
        Log.d(TAG, "News Country: $countryCode")
        afterCallViewModel.fetchNews(countryCode,"business","03125cd5b45a4bba9d378fb071d86003")
    }
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).apply {
            setMinUpdateIntervalMillis(5000) // 5 seconds
        }.build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Use the location data
            }
        }
    }

    private fun initObserver(){
// When you get the new data from the ViewModel observer
        afterCallViewModel.weatherData.observe(viewLifecycleOwner) { result ->
            Log.d(TAG,"weather data: $result")
            when (result) {
                is Result.Success<WeatherResponse> -> {
                    val data = result.data
                    weatherData = data.days
                  //  newsAdapter.notifyDataSetChanged() // Notify adapter that the data changed
                    getNews(countryCode)
                }else->{
                }
                // Handle other cases (Error, Loading, etc.)
            }
        }

// Similarly for news data
        afterCallViewModel.newsData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<NewsResponse> -> {
                    val data = result.data
                   // newsAdapter.newsList = data.articles
                  //  newsAdapter.notifyDataSetChanged()
                    if(result.data.articles.isEmpty()){
                        getNews("us")
                    }else{
                        Log.d(TAG, "Render rec view")
                        val  newsAdapter = NewsAdapter(
                            requireContext(),
                            data.articles,  // Initially empty news list
                            weatherData,         // Initially no weather data
                            appCustomView,
                            userLocation,
                          object : NewsAdapter.OnItemClickListener {
                              override fun onItemClick(position: Int, newsArticle: NewsArticle) {
                                  val intent = Intent(Intent.ACTION_VIEW).apply {
                                      this.data = Uri.parse(newsArticle.url) // Assuming 'url' is a field in NewsArticle
                                  }
                                  context?.startActivity(intent)
                              }
                          }
                        )
                     //   binding.newsRec.adapter = newsAdapter
                        binding.newsRec.apply {
                            layoutManager = LinearLayoutManager(requireActivity())
                            adapter = newsAdapter
                        }
                    }
                }else->{
                }
                // Handle other cases (Error, Loading, etc.)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeatherCardFragment().apply {
                arguments = Bundle().apply {
                    putString(param1, param1)
                    putString(param2, param2)
                }
            }
    }
}