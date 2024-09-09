package com.appcapital.call_library

import com.appcapital.call_library.api.NewsApiService
import com.appcapital.call_library.api.WeatherApiService
import com.appcapital.call_library.respository.AfterCallNewsRepository
import com.appcapital.call_library.respository.AfterCallWeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideWeatherService(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
    @Provides
    fun provideNewsService(): NewsApiService {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
    @Provides
    fun provideAfterCallWeatherRepository(service: WeatherApiService): AfterCallWeatherRepository {
        return AfterCallWeatherRepository(service)
    }
    @Provides
    fun provideAfterCallNewsRepository(service: NewsApiService): AfterCallNewsRepository {
        return AfterCallNewsRepository(service)
    }

}
//
//package com.appcapital.call_library
//
//import com.appcapital.call_library.api.ApiService
//import com.appcapital.call_library.respository.AfterCallRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    fun provideRetrofit(): Retrofit {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl("https://weather.visualcrossing.com/")
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    fun provideApiService(retrofit: Retrofit): ApiService {
//        return retrofit.create(ApiService::class.java)
//    }
//
//    @Provides
//    fun provideAfterCallRepository(apiService: ApiService): AfterCallRepository {
//        return AfterCallRepository(apiService)
//    }
//}