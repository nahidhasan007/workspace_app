package com.hasan.collabworkspace.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    // Note: In a real app, WorkspaceRepository would be injected via Hilt or Koin
    // For this example without DI framework configured yet, we assume it's passed or retrieved
    // Using a factory or service locator.
    private val repository: WorkspaceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
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
