package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class DeleteNoteUseCase(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(id: String, soft: Boolean = true) {
        repository.deleteNote(id, soft)
    }
}