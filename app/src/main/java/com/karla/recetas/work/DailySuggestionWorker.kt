package com.karla.recetas.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.karla.recetas.R

class DailySuggestionWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
  override suspend fun doWork(): Result {
    val text = "Sugerencia del día: prueba combinar arroz y pollo"
    val notif = NotificationCompat.Builder(applicationContext, "sugerencias")
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle("Recetas Gemini")
      .setContentText(text)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .build()
    NotificationManagerCompat.from(applicationContext).notify(1001, notif)
    return Result.success()
  }
}