package com.hasan.collabworkspace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hasan.collabworkspace.data.local.dao.WorkspaceDao
import com.hasan.collabworkspace.data.local.entity.AssetEntity
import com.hasan.collabworkspace.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class, AssetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract val dao: WorkspaceDao
}
