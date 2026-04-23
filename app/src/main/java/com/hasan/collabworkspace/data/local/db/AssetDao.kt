package com.hasan.collabworkspace.data.local.db

import androidx.room.*
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets WHERE noteId = :noteId AND isDeleted = 0")
    fun getAssetsForNote(noteId: String): Flow<List<AssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(asset: AssetEntity)

    @Update
    suspend fun update(asset: AssetEntity)

    @Query("UPDATE assets SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun hardDelete(id: String)
}