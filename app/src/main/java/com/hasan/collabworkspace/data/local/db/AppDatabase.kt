package com.hasan.collabworkspace.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity

@Database(
    entities = [NoteEntity::class, AssetEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun assetDao(): AssetDao
}