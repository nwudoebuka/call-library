package com.appcapital.call_library.respository

import android.util.Log
import com.appcapital.call_library.api.NewsApiService
import com.appcapital.call_library.api.Result
import com.appcapital.call_library.api.WeatherApiService
import com.appcapital.call_library.model.NewsResponse
import com.appcapital.call_library.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AfterCallNewsRepository(private val newsApiService: NewsApiService) {

    suspend fun getNews(country: String, category: String, apiKey: String): Result<NewsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = newsApiService.getTopHeadlines(country, category, apiKey)
                if (response.isSuccessful) {
                    Result.Success(response.body()!!)
                } else {
                    Log.e("AfterCallRepository", "Response code: ${response.code()}, message: ${response.message()}")
                    Result.Error(Exception("Failed to get News. Code: ${response.code()}, Message: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}