package com.hasan.collabworkspace.presentation.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.hasan.collabworkspace.presentation.ui.notes.NoteScreen
import com.hasan.collabworkspace.presentation.mvi.WorkspaceViewModel

@Composable
fun MultiNoteTabs(viewModel: WorkspaceViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val notes = viewModel.state.collectAsState().value.notes

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            notes.forEachIndexed { index, note ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(note.content.take(10)) }
                )
            }
        }
        if (notes.isNotEmpty()) {
            NoteScreen(viewModel = viewModel)
        }
    }
}