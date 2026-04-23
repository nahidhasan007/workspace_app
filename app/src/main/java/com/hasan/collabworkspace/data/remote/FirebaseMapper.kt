package com.hasan.collabworkspace.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity

object FirestoreMapper {

    fun toNoteEntity(doc: DocumentSnapshot): NoteEntity? {
        val data = doc.data ?: return null
        return NoteEntity(
            id = doc.id,
            content = data["content"] as? String ?: "",
            order = (data["order"] as? Long)?.toInt() ?: 0,
            lastModified = data["lastModified"] as? Long ?: System.currentTimeMillis(),
            version = (data["version"] as? Long)?.toInt() ?: 1,
            isDeleted = data["isDeleted"] as? Boolean ?: false
        )
    }

    fun fromNoteEntity(note: NoteEntity): Map<String, Any> {
        return mapOf(
            "content" to note.content,
            "order" to note.order,
            "lastModified" to note.lastModified,
            "version" to note.version,
            "isDeleted" to note.isDeleted
        )
    }

    fun toAssetEntity(doc: DocumentSnapshot): AssetEntity? {
        val data = doc.data ?: return null
        return AssetEntity(
            id = doc.id,
            noteId = data["noteId"] as? String ?: "",
            uri = data["uri"] as? String ?: "",
            posX = (data["posX"] as? Double)?.toFloat() ?: 0f,
            posY = (data["posY"] as? Double)?.toFloat() ?: 0f,
            rotation = (data["rotation"] as? Double)?.toFloat() ?: 0f,
            scale = (data["scale"] as? Double)?.toFloat() ?: 1f,
            lastModified = data["lastModified"] as? Long ?: System.currentTimeMillis(),
            version = (data["version"] as? Long)?.toInt() ?: 1,
            isDeleted = data["isDeleted"] as? Boolean ?: false
        )
    }

    fun fromAssetEntity(asset: AssetEntity): Map<String, Any> {
        return mapOf(
            "noteId" to asset.noteId,
            "uri" to asset.uri,
            "posX" to asset.posX,
            "posY" to asset.posY,
            "rotation" to asset.rotation,
            "scale" to asset.scale,
            "lastModified" to asset.lastModified,
            "version" to asset.version,
            "isDeleted" to asset.isDeleted
        )
    }
}