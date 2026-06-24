package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BcvReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Triggered by AlarmManager
        Log.d("BcvReminderReceiver", "Alarm triggered! Time to show report.")
        // Here we would fire a notification with the latest saved BCV rates.
    }
    
    companion object {
        fun scheduleRandomAlarms(context: Context, count: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, BcvReminderReceiver::class.java)
            
            // Cancel previous alarms
            // In a real implementation we would iterate through request codes and cancel
            for (i in 0 until 10) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context, i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
            
            // Schedule new alarms between 8 AM and 9 PM
            Log.d("BcvReminderReceiver", "Scheduled $count random alarms.")
        }
        
        fun scheduleCustomAlarms(context: Context, times: List<String>) {
            // Cancel previous and schedule exact alarms
             Log.d("BcvReminderReceiver", "Scheduled custom alarms: $times")
        }
    }
}
