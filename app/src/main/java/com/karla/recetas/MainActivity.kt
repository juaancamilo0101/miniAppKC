package com.karla.recetas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.karla.recetas.databinding.ActivityMainBinding
import com.karla.recetas.work.DailySuggestionWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    supportFragmentManager.commit {
      replace(R.id.container, SelectionFragment())
    }
    createChannel()
    scheduleDaily()
  }

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel("sugerencias", "Sugerencias", NotificationManager.IMPORTANCE_DEFAULT)
      val nm = getSystemService(NotificationManager::class.java)
      nm.createNotificationChannel(channel)
    }
  }

  private fun scheduleDaily() {
    val work = PeriodicWorkRequestBuilder<DailySuggestionWorker>(24, TimeUnit.HOURS).build()
    WorkManager.getInstance(this).enqueueUniquePeriodicWork("sugerencia_diaria", ExistingPeriodicWorkPolicy.UPDATE, work)
  }
}