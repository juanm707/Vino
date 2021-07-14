package com.example.vino.network

import android.util.FloatMath
import com.squareup.moshi.Json

class WeatherBasic (
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: Current,
    @Json(name = "daily")val dailyTemperatures: List<Daily>
)

class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Float,
    val feels_like: Float,
    val pressure: Float,
    val humidity: Float,
    val dew_point: Float,
    val uvi: Float,
    val clouds: Float,
    val visibility: Float,
    val wind_speed: Float,
    val wind_deg: Float,
    val wind_gust: Float?,
    val weather: List<Weather>,
)

class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Float,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Float,
    val humidity: Float,
    val dew_point: Float,
    val wind_speed: Float,
    val wind_deg: Float,
    val wind_gust: Float,
    val weather: List<Weather>,
    val clouds: Float,
    val pop: Float,
    val uvi: Float
)

class Temp(
    val day: Float,
    val min: Float,
    val max: Float,
    val night: Float,
    val eve: Float,
    val morn: Float
)

class FeelsLike(
    val day: Float,
    val night: Float,
    val eve: Float,
    val morn: Float
)

class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
