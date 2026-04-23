package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import com.hasan.collabworkspace.data.local.entities.NoteEntity

class SyncNotesUseCase(
    private val repository: WorkspaceRepository
) {
    fun listenRemoteNotes(): Flow<List<NoteEntity>> = repository.listenRemoteNotes()
}