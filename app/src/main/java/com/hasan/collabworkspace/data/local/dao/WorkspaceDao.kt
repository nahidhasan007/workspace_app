package com.hasan.collabworkspace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hasan.collabworkspace.data.local.entity.AssetEntity
import com.hasan.collabworkspace.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {

    // --- Notes ---
    @Query("SELECT * FROM notes WHERE tabId = :tabId AND isDeleted = 0 ORDER BY orderIndex ASC")
    fun observeNotesByTab(tabId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE isPendingSync = 1")
    suspend fun getPendingNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, isPendingSync = 1, lastModified = :timestamp, version = version + 1 WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: String, timestamp: Long)
    
    @Query("UPDATE notes SET isPendingSync = 0 WHERE id = :noteId")
    suspend fun clearNotePendingSync(noteId: String)

    // --- Assets ---
    @Query("SELECT * FROM assets WHERE tabId = :tabId AND isDeleted = 0")
    fun observeAssetsByTab(tabId: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :assetId")
    suspend fun getAssetById(assetId: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE isPendingSync = 1")
    suspend fun getPendingAssets(): List<AssetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAsset(asset: AssetEntity)

    @Query("UPDATE assets SET isDeleted = 1, isPendingSync = 1, lastModified = :timestamp, version = version + 1 WHERE id = :assetId")
    suspend fun softDeleteAsset(assetId: String, timestamp: Long)

    @Query("UPDATE assets SET isPendingSync = 0 WHERE id = :assetId")
    suspend fun clearAssetPendingSync(assetId: String)
}
