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

    lateinit var useCases: WorkspaceUseCases
    lateinit var repository: WorkspaceRepositoryImpl

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            WorkspaceDatabase::class.java,
            "workspace_db"
        ).fallbackToDestructiveMigration()
            .build()

        var firestoreDataSource: FirestoreDataSource? = null
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestoreDataSource = FirestoreDataSource(firestore)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        repository = WorkspaceRepositoryImpl(
            dao = database.dao,
            remote = firestoreDataSource
        )

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

        WorkManagerSetup.scheduleSyncWork(this)
    }
}
