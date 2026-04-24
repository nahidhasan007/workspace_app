package com.hasan.collabworkspace.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hasan.collabworkspace.CollabWorkspaceApp

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? CollabWorkspaceApp ?: return Result.failure()
        val repository = app.repository

        return try {
            // Push all pending local changes to Firebase
            repository.syncPendingChanges()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If it fails (e.g., still offline, or server error), retry later
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "SyncWorkspaceWorker"
    }
}
