package com.appcapital.call_library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcapital.call_library.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.appcapital.call_library.api.Result
import com.appcapital.call_library.model.NewsResponse
import com.appcapital.call_library.respository.AfterCallNewsRepository
import com.appcapital.call_library.respository.AfterCallWeatherRepository

@HiltViewModel
class AfterCallViewModel @Inject constructor(
    private val afterCallWeatherRepository: AfterCallWeatherRepository,
    private val afterCallNewsRepository: AfterCallNewsRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<Result<WeatherResponse>>()
    val weatherData: LiveData<Result<WeatherResponse>> get() = _weatherData
    private val _newsData = MutableLiveData<Result<NewsResponse>>()
    val newsData: LiveData<Result<NewsResponse>> get() = _newsData

    fun fetchWeather(location: String, startDate: String, endDate: String, apiKey: String) {
        viewModelScope.launch {
            // This will perform the network call on a background thread and post the result to LiveData
            val result = afterCallWeatherRepository.getWeather(location, startDate, endDate, apiKey)
            _weatherData.postValue(result)
        }
    }

    fun fetchNews(country: String, category: String, apiKey: String) {
        viewModelScope.launch {
            // This will perform the network call on a background thread and post the result to LiveData
            val result = afterCallNewsRepository.getNews(country, category, apiKey)
            _newsData.postValue(result)
        }
    }
}