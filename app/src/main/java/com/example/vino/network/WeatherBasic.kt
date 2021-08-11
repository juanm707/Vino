package com.example.vino.network

import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

class WeatherBasic (
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: Current,
    @Json(name = "daily") val dailyTemperatures: List<Daily>?,
    @Json(name="hourly") val hourlyTemperatures: List<Hourly>?,
    val alerts: List<Alert>?
)

open class Forecast() {
    open fun time(): String {
        return "-/-"
    }

    open fun temp(): Float {
        return 0f
    }
}

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
    val uvi: Float,
) : Forecast() {
    override fun time(): String {
        val sdf = SimpleDateFormat("M/d", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(dt * 1000L))
    }

    override fun temp(): Float {
        return temp.max
    }
}

class Hourly(
    val dt: Long,
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
    val wind_gust: Float,
    val weather: List<Weather>,
    val pop: Float,
) : Forecast() {
    override fun time(): String {
        val sdf = SimpleDateFormat("ha", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(dt * 1000L))
    }

    override fun temp(): Float {
        return temp
    }
}

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

class Alert(
    @Json(name = "sender_name") val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)
