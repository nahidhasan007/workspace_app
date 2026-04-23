package com.hasan.collabworkspace.sync

import android.content.Context
import androidx.compose.ui.unit.Constraints
import androidx.work.*

object SyncManager {

    private const val SYNC_WORK_NAME = "workspace_sync"

    /*fun schedulePeriodicSync(context: Context) {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun triggerOneTimeSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }*/
}