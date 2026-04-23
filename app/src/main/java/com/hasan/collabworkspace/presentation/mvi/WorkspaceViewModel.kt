package com.hasan.collabworkspace.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.collabworkspace.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkspaceViewModel(
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val addAssetUseCase: AddAssetUseCase,
    private val updateAssetTransformUseCase: UpdateAssetTransformUseCase,
    private val syncNotesUseCase: SyncNotesUseCase,
    private val resolveConflictUseCase: ResolveConflictUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkspaceState())
    val state: StateFlow<WorkspaceState> = _state

    fun processIntent(intent: WorkspaceIntent) {
        when (intent) {
            is WorkspaceIntent.CreateNote -> viewModelScope.launch {
                createNoteUseCase(intent.content, intent.order)
            }
            is WorkspaceIntent.UpdateNote -> viewModelScope.launch {
                val note = _state.value.notes.find { it.id == intent.noteId }
                note?.let { updateNoteUseCase(it, intent.newContent) }
            }
            is WorkspaceIntent.DeleteNote -> viewModelScope.launch {
                deleteNoteUseCase(intent.noteId, intent.soft)
            }
            is WorkspaceIntent.AddAsset -> viewModelScope.launch {
                addAssetUseCase(intent.noteId, intent.uri)
            }
            is WorkspaceIntent.UpdateAssetTransform -> viewModelScope.launch {
                val asset = _state.value.assets.values.flatten().find { it.id == intent.assetId }
                asset?.let {
                    updateAssetTransformUseCase(it, intent.x, intent.y, intent.rotation, intent.scale)
                }
            }
            is WorkspaceIntent.DeleteAsset -> viewModelScope.launch {
                // deletion handled via repository
            }
            WorkspaceIntent.SyncNotes -> viewModelScope.launch {
                syncNotesUseCase.listenRemoteNotes().collect { remoteNotes ->
                    _state.value = _state.value.copy(notes = remoteNotes)
                }
            }
            WorkspaceIntent.SyncAssets -> { /* similar to notes */ }
            is WorkspaceIntent.ResolveNoteConflict -> viewModelScope.launch {
                val local = _state.value.notes.find { it.id == intent.localId }
                val remote = _state.value.notes.find { it.id == intent.remoteId }
                if (local != null && remote != null) {
                    val resolved = resolveConflictUseCase.resolveNote(local, remote)
                    _state.value = _state.value.copy(
                        notes = _state.value.notes.map {
                            if (it.id == resolved.id) resolved else it
                        },
                        conflictNotes = null
                    )
                }
            }
            is WorkspaceIntent.ResolveAssetConflict -> viewModelScope.launch {
                val local = _state.value.assets.values.flatten().find { it.id == intent.localId }
                val remote = _state.value.assets.values.flatten().find { it.id == intent.remoteId }
                if (local != null && remote != null) {
                    val resolved = resolveConflictUseCase.resolveAsset(local, remote)
                    val updatedAssets = _state.value.assets.toMutableMap()
                    updatedAssets[resolved.noteId] = updatedAssets[resolved.noteId]
                        ?.map { if (it.id == resolved.id) resolved else it } ?: listOf(resolved)
                    _state.value = _state.value.copy(
                        assets = updatedAssets,
                        conflictAssets = null
                    )
                }
            }
        }
    }
}