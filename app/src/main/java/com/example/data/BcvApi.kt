package com.example.data

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class BcvResponse(
    val monitors: Map<String, MonitorData>?
)

@JsonClass(generateAdapter = true)
data class MonitorData(
    val price: Double?,
    val title: String?
)

interface BcvApi {
    @GET("api/v1/dollar?page=bcv")
    suspend fun getRates(): BcvResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://pydolarvenezuela-api.vercel.app/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    val api: BcvApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BcvApi::class.java)
    }
}
