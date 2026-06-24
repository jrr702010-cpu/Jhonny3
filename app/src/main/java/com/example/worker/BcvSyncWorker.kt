package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.RetrofitInstance
import android.util.Log

class BcvSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = RetrofitInstance.api.getRates()
            val usdPrice = response.monitors?.get("usd")?.price
            val eurPrice = response.monitors?.get("eur")?.price
            
            // Here we would compare with local SharedPreferences to check for fluctuations
            // and trigger system notifications if they exceed the user's threshold.
            
            Log.d("BcvSyncWorker", "Synced: USD=$usdPrice EUR=$eurPrice")
            
            Result.success()
        } catch (e: Exception) {
            Log.e("BcvSyncWorker", "Failed to sync", e)
            Result.retry()
        }
    }
}
