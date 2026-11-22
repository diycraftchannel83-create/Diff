package com.winsmediatam.diff

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingWorkPolicy

class OfflineModelDownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        createNotificationChannel()
        setForeground(createForegroundInfo("Preparing model download", 0, 0))

        return try {
            val model = OfflineModelDownloader.downloadAndInstall(applicationContext) { downloaded, total ->
                setProgressAsync(
                    Data.Builder()
                        .putLong(KEY_DOWNLOADED, downloaded)
                        .putLong(KEY_TOTAL, total)
                        .putInt(KEY_PERCENT, if (total > 0) ((downloaded * 100) / total).toInt().coerceIn(0, 100) else 0)
                        .build()
                )
                val percent = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                setForegroundAsync(createForegroundInfo("Downloading model: $percent%", downloaded, total))
            }
            setForeground(ForegroundInfo(NOTIFICATION_ID, createNotification("Model installed: ${model.name}", 100, false)))
            Result.success(Data.Builder().putString(KEY_MODEL_NAME, model.name).build())
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Model downloads", NotificationManager.IMPORTANCE_LOW)
                    .apply { description = "Offline AI model download progress" }
            )
        }
    }

    private fun createForegroundInfo(message: String, downloaded: Long, total: Long): ForegroundInfo {
        val percent = if (total > 0) ((downloaded * 100) / total).toInt().coerceIn(0, 100) else 0
        return ForegroundInfo(NOTIFICATION_ID, createNotification(message, percent, total <= 0))
    }

    private fun createNotification(message: String, percent: Int, indeterminate: Boolean): Notification =
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Diff AI Studio")
            .setContentText(message)
            .setProgress(100, percent, indeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()

    companion object {
        const val WORK_NAME = "diff_offline_model_download"
        const val KEY_DOWNLOADED = "downloaded_bytes"
        const val KEY_TOTAL = "total_bytes"
        const val KEY_PERCENT = "percent"
        const val KEY_MODEL_NAME = "model_name"
        private const val CHANNEL_ID = "diff_model_downloads"
        private const val NOTIFICATION_ID = 4815

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<OfflineModelDownloadWorker>()
                .addTag(WORK_NAME)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
