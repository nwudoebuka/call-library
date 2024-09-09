package com.appcapital.call_library.api
import com.appcapital.call_library.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiService {
    @GET("timeline/{location}/{startDate}/{endDate}")
    suspend fun getWeather(
        @Path("location") location: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("key") apiKey: String
    ): Response<WeatherResponse>
}