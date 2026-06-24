package com.example.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class BcvResponse(
    val monitors: Map<String, MonitorData>?
)

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

    val api: BcvApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BcvApi::class.java)
    }
}
