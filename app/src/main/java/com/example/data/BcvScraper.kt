package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object BcvScraper {
    private const val BCV_URL = "https://www.bcv.org.ve/"

    private val unsafeClient: OkHttpClient by lazy {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        } catch (e: Exception) {
            Log.e("BcvScraper", "Error creating unsafe OkHttpClient, falling back to standard client", e)
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    suspend fun getRates(): Pair<Double?, Double?> {
        return try {
            val request = Request.Builder()
                .url(BCV_URL)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .build()

            val responseBody = withContext(Dispatchers.IO) {
                unsafeClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("BcvScraper", "Unexpected response code: ${response.code}")
                        null
                    } else {
                        response.body?.string()
                    }
                }
            }

            if (responseBody != null) {
                val usdRate = parseRate(responseBody, "dolar")
                val eurRate = parseRate(responseBody, "euro")
                Log.d("BcvScraper", "Parsed rates from BCV - USD: $usdRate, EUR: $eurRate")
                Pair(usdRate, eurRate)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e("BcvScraper", "Error scraping BCV website", e)
            Pair(null, null)
        }
    }

    private fun parseRate(html: String, id: String): Double? {
        try {
            val idIndex = html.indexOf("id=\"$id\"")
            val altIdIndex = if (idIndex == -1) html.indexOf("id='$id'") else idIndex
            if (altIdIndex == -1) return null

            val windowStart = altIdIndex + id.length + 4
            val windowEnd = (windowStart + 150).coerceAtMost(html.length)
            if (windowStart >= html.length) return null
            val window = html.substring(windowStart, windowEnd)

            val pattern = Pattern.compile("([0-9]+[.,][0-9]+)", Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(window)
            if (matcher.find()) {
                val valueStr = matcher.group(1)?.trim() ?: return null
                val cleanValue = valueStr.replace("\\s".toRegex(), "")

                val formattedValue = if (cleanValue.contains(",") && !cleanValue.contains(".")) {
                    cleanValue.replace(",", ".")
                } else if (cleanValue.contains(".") && cleanValue.contains(",")) {
                    if (cleanValue.lastIndexOf(",") > cleanValue.lastIndexOf(".")) {
                        cleanValue.replace(".", "").replace(",", ".")
                    } else {
                        cleanValue.replace(",", "")
                    }
                } else {
                    cleanValue
                }
                return formattedValue.toDoubleOrNull()
            }
        } catch (e: Exception) {
            Log.e("BcvScraper", "Error parsing rate for id: $id", e)
        }
        return null
    }
}
