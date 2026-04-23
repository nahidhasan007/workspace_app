package com.hasan.collabworkspace.data.local.db

import androidx.room.*
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY `order` ASC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: String): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun hardDelete(id: String)
}