package com.appcapital.call_library.respository

import android.util.Log
import com.appcapital.call_library.api.WeatherApiService
import com.appcapital.call_library.model.WeatherResponse
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.appcapital.call_library.api.Result

class AfterCallWeatherRepository(private val weatherApiService: WeatherApiService) {

    suspend fun getWeather(location: String, startDate: String, endDate: String, apiKey: String): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeather(location, startDate, endDate, apiKey)
                if (response.isSuccessful) {
                    Result.Success(response.body()!!)
                } else {
                    Log.e("AfterCallRepository", "Response code: ${response.code()}, message: ${response.message()}")
                    Result.Error(Exception("Failed to get weather. Code: ${response.code()}, Message: ${response.message()}"))

                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}