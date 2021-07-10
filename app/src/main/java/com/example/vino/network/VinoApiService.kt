package com.example.vino.network

import com.example.vino.model.Block
import com.example.vino.model.LWPReading
import com.example.vino.model.Todo
import com.example.vino.model.Vineyard
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class VinoApiStatus { LOADING, ERROR, DONE }

private const val BASE_URL = "http://10.0.2.2:8000" // for actual device use 10.0.0.37
private const val OPEN_WEATHER_URL = "https://api.openweathermap.org"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

private val weatherRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(OPEN_WEATHER_URL)
    .build()

interface VinoApiService {
    /**
     * Returns a [VineyardManagerUser] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "vineyards.json" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("user.json")
    suspend fun getUser() : VineyardManagerUser

    @GET("vineyards.json")
    suspend fun getVineyards() : List<Vineyard>

    @GET("todos.json")
    suspend fun getTodos() : MutableList<Todo>

    @GET("blocks.json")
    suspend fun getBlocks() : List<BlockCoordinates>

    @GET("lwpreadings.json")
    suspend fun getLWPReadings() : List<LWPReading>
}

interface VinoWeatherService {

    @GET("data/2.5/onecall?exclude=minutely,hourly,alerts&units=imperial&appid=")
    suspend fun getDailyWeather(@Query("lat") latitude: Double, @Query("lon") longitude: Double) : WeatherBasic
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object VinoApi {
    val retrofitService: VinoApiService by lazy { retrofit.create(VinoApiService::class.java) }
    val retrofitWeatherService: VinoWeatherService by lazy { weatherRetrofit.create(VinoWeatherService::class.java) }
}