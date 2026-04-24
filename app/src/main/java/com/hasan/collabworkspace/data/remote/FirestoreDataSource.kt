package com.hasan.collabworkspace.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hasan.collabworkspace.data.remote.dto.AssetDto
import com.hasan.collabworkspace.data.remote.dto.NoteDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val firestore: FirebaseFirestore
) {
    private val notesCollection = firestore.collection("notes")
    private val assetsCollection = firestore.collection("assets")

    // --- Notes ---
    fun observeNotes(tabId: String): Flow<List<NoteDto>> = callbackFlow {
        val listener = notesCollection
            .whereEqualTo("tabId", tabId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val notes = snapshot?.documents?.mapNotNull { it.toObject(NoteDto::class.java) } ?: emptyList()
                trySend(notes)
            }

        awaitClose { listener.remove() }
    }

    suspend fun pushNote(note: NoteDto) {
        notesCollection.document(note.id).set(note, SetOptions.merge()).await()
    }

    // --- Assets ---
    fun observeAssets(tabId: String): Flow<List<AssetDto>> = callbackFlow {
        val listener = assetsCollection
            .whereEqualTo("tabId", tabId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val assets = snapshot?.documents?.mapNotNull { it.toObject(AssetDto::class.java) } ?: emptyList()
                trySend(assets)
            }

        awaitClose { listener.remove() }
    }

    suspend fun pushAsset(asset: AssetDto) {
        assetsCollection.document(asset.id).set(asset, SetOptions.merge()).await()
    }
}
