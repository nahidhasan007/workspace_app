package com.hasan.collabworkspace.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hasan.collabworkspace.data.repository.WorkspaceRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: WorkspaceRepositoryImpl
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync notes
            repository.getAllNotes().collect { notes ->
                notes.forEach { note ->
                    repository.pushNoteRemote(note)
                }
            }

            // Sync assets
            repository.getAssetsForNote("").collect { assets ->
                assets.forEach { asset ->
                    repository.pushAssetRemote(asset)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}