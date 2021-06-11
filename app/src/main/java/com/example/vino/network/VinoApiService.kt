package com.example.vino.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://10.0.2.2:8000" // for actual device use 10.0.0.37

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

/**
 * A public interface that exposes the [getUser] method
 */
interface VinoApiService {
    /**
     * Returns a [VineyardManagerUser] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "vineyards.json" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("vineyards.json")
    suspend fun getUser() : VineyardManagerUser

    @GET("todos.json")
    suspend fun getTodos() : List<Todo>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object VinoApi {
    val retrofitService: VinoApiService by lazy { retrofit.create(VinoApiService::class.java) }
}