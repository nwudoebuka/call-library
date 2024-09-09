package com.appcapital.call_library.model

import com.google.gson.JsonArray
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*


import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class WeatherResponse (
    val queryCost: Long,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Long,
    val description: String,
    val days: List<CurrentConditions>,
    val alerts: List<Alert>,
    val stations: Map<String, Station>,
    val currentConditions: CurrentConditions
)

@Serializable
data class Alert (
    val event: String,
    val headline: String,
    val ends: String,
    val endsEpoch: Long,
    val onset: String,
    val onsetEpoch: Long,
    val id: String,
    val language: String,
    val link: String,
    val description: String
)

@Serializable
data class CurrentConditions (
    val datetime: String,
    val datetimeEpoch: Long,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val dew: Double,
    val precipprob: Double,
    val snow: Long,
    val snowdepth: Long,
    val preciptype: List<String>? = null,
    val windgust: Double,
    val windspeed: Double,
    val winddir: Double,
    val pressure: Double,
    val visibility: Double,
    val cloudcover: Double,
    val solarradiation: Double,
    val solarenergy: Double,
    val uvindex: Long,
    val conditions: String? = null,
    val icon: Icon,
    val stations: List<ID>? = null,
    val source: Source,
    val sunrise: String? = null,
    val sunriseEpoch: Long? = null,
    val sunset: String? = null,
    val sunsetEpoch: Long? = null,
    val moonphase: Double? = null,
    val tempmax: Double? = null,
    val tempmin: Double? = null,
    val feelslikemax: Double? = null,
    val feelslikemin: Double? = null,
    val severerisk: Long? = null,
    val description: String? = null,
    val hours: List<CurrentConditions>? = null
)

@Serializable
enum class Conditions(val value: String) {
    @SerialName("Clear") Clear("Clear"),
    @SerialName("Partially cloudy") PartiallyCloudy("Partially cloudy");
}

@Serializable
enum class Icon(val value: String) {
    @SerialName("clear-day") ClearDay("clear-day"),
    @SerialName("clear-night") ClearNight("clear-night"),
    @SerialName("partly-cloudy-day") PartlyCloudyDay("partly-cloudy-day"),
    @SerialName("partly-cloudy-night") PartlyCloudyNight("partly-cloudy-night");
}

@Serializable
enum class Source(val value: String) {
    @SerialName("comb") Comb("comb"),
    @SerialName("fcst") Fcst("fcst"),
    @SerialName("obs") Obs("obs");
}

@Serializable
enum class ID(val value: String) {
    @SerialName("D6801") D6801("D6801"),
    @SerialName("E4498") E4498("E4498"),
    @SerialName("EKCH") Ekch("EKCH"),
    @SerialName("EKRK") Ekrk("EKRK"),
    @SerialName("ESMS") Esms("ESMS"),
    @SerialName("ESTL") Estl("ESTL"),
    @SerialName("F8306") F8306("F8306");
}

@Serializable
data class Station (
    val distance: Long,
    val latitude: Double,
    val longitude: Double,
    val useCount: Long,
    val id: ID,
    val name: String,
    val quality: Long,
    val contribution: Long
)
