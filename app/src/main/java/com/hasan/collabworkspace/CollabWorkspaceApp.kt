package com.hasan.collabworkspace

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.hasan.collabworkspace.data.local.WorkspaceDatabase
import com.hasan.collabworkspace.data.remote.FirestoreDataSource
import com.hasan.collabworkspace.data.repository.WorkspaceRepositoryImpl
import com.hasan.collabworkspace.data.worker.WorkManagerSetup
import com.hasan.collabworkspace.domain.usecase.*

class CollabWorkspaceApp : Application() {

    // Manual Dependency Injection container
    lateinit var useCases: WorkspaceUseCases
    lateinit var repository: WorkspaceRepositoryImpl

    override fun onCreate() {
        super.onCreate()

        // Init Room Database
        val database = Room.databaseBuilder(
            this,
            WorkspaceDatabase::class.java,
            "workspace_db"
        ).build()

        // Init Firestore (with fallback for purely local run)
        var firestoreDataSource: FirestoreDataSource? = null
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestoreDataSource = FirestoreDataSource(firestore)
        } catch (e: Exception) {
            e.printStackTrace()
            // Firebase not configured properly, will run purely offline
        }

        // Init Repository
        repository = WorkspaceRepositoryImpl(
            dao = database.dao,
            remote = firestoreDataSource
        )

        // Init Use Cases
        useCases = WorkspaceUseCases(
            observeNotes = ObserveNotesUseCase(repository),
            observeAssetsByTab = ObserveAssetsByTabUseCase(repository),
            saveNote = SaveNoteUseCase(repository),
            saveAsset = SaveAssetUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            deleteAsset = DeleteAssetUseCase(repository),
            syncWorkspace = SyncWorkspaceUseCase(repository),
            resolveConflict = ResolveConflictUseCase(repository)
        )

        // Schedule background sync
        WorkManagerSetup.scheduleSyncWork(this)
    }
}
