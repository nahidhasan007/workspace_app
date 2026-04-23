package com.hasan.collabworkspace.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val notesCollection = firestore.collection("notes")
    private val assetsCollection = firestore.collection("assets")

    // Listen for real-time updates from Firestore
    fun listenNotes(): Flow<List<NoteEntity>> = callbackFlow {
        val registration: ListenerRegistration = notesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val notes = snapshot?.documents?.mapNotNull { FirestoreMapper.toNoteEntity(it) } ?: emptyList()
            trySend(notes)
        }
        awaitClose { registration.remove() }
    }

    fun listenAssets(): Flow<List<AssetEntity>> = callbackFlow {
        val registration: ListenerRegistration = assetsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val assets = snapshot?.documents?.mapNotNull { FirestoreMapper.toAssetEntity(it) } ?: emptyList()
            trySend(assets)
        }
        awaitClose { registration.remove() }
    }

    // Push updates to Firestore
    suspend fun pushNote(note: NoteEntity) {
        notesCollection.document(note.id).set(FirestoreMapper.fromNoteEntity(note))
    }

    suspend fun pushAsset(asset: AssetEntity) {
        assetsCollection.document(asset.id).set(FirestoreMapper.fromAssetEntity(asset))
    }
}