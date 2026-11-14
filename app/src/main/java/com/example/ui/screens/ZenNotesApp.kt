package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ZenBottomNavigationBar
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.NotesUiState
import com.example.ui.viewmodel.NotesViewModel

@Composable
fun ZenNotesApp(
    uiState: NotesUiState,
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Left Directory / Tab Pane
                Box(
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                ) {
                    MainTabScaffold(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }

                VerticalDivider(thickness = 1.dp, color = palette.border)

                // Right Writing Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(palette.background)
                ) {
                    val activeNote = uiState.activeNote
                    if (activeNote != null) {
                        ImmersiveEditorScreen(
                            note = activeNote,
                            notebooks = uiState.notebooks,
                            isPreviewMode = uiState.isPreviewMode,
                            zenFocusMode = uiState.zenFocusMode,
                            canUndo = uiState.canUndo,
                            canRedo = uiState.canRedo,
                            showBackButton = false,
                            onBack = viewModel::closeEditor,
                            onTitleChange = viewModel::updateActiveNoteTitle,
                            onProseChange = viewModel::updateActiveNoteProse,
                            onTogglePreview = viewModel::togglePreviewMode,
                            onToggleZenFocus = viewModel::toggleZenFocusMode,
                            onCycleStatus = viewModel::cycleActiveNoteStatus,
                            onChangeNotebook = viewModel::updateActiveNoteNotebook,
                            onAddTag = viewModel::addActiveNoteTag,
                            onRemoveTag = viewModel::removeActiveNoteTag,
                            onToggleChecklistItem = { lineIdx ->
                                viewModel.toggleNoteChecklistItem(activeNote, lineIdx)
                            },
                            onAddChecklistItem = viewModel::addChecklistItemToActiveNote,
                            onRemoveChecklistItem = viewModel::removeChecklistItemFromActiveNote,
                            onInsertMarkdown = viewModel::insertMarkdownIntoProse,
                            onUndo = viewModel::undo,
                            onRedo = viewModel::redo,
                            onTogglePin = { viewModel.togglePin(activeNote) },
                            onToggleArchive = { viewModel.toggleArchive(activeNote) },
                            onExportPdf = { viewModel.exportNoteAsPdf(activeNote) },
                            onExportScrollImage = { viewModel.exportNoteAsLongScrollImage(activeNote) },
                            onDelete = { viewModel.deleteNote(activeNote) }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ma (間)",
                                    fontFamily = NewsreaderFontFamily,
                                    fontSize = 30.sp,
                                    color = palette.subtleInk
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Select a thought from the archive or tap the pen icon to begin a new composition.",
                                    fontFamily = InterFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp,
                                    color = palette.mutedInk,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Mobile Single-Screen Flow
            BackHandler(enabled = uiState.isEditorOpen || uiState.activeTab != AppTab.NOTES) {
                if (uiState.isEditorOpen) {
                    viewModel.closeEditor()
                } else if (uiState.activeTab != AppTab.NOTES) {
                    viewModel.selectTab(AppTab.NOTES)
                }
            }

            if (uiState.isEditorOpen && uiState.activeNote != null) {
                val activeNote = uiState.activeNote
                ImmersiveEditorScreen(
                    note = activeNote,
                    notebooks = uiState.notebooks,
                    isPreviewMode = uiState.isPreviewMode,
                    zenFocusMode = uiState.zenFocusMode,
                    canUndo = uiState.canUndo,
                    canRedo = uiState.canRedo,
                    showBackButton = true,
                    onBack = viewModel::closeEditor,
                    onTitleChange = viewModel::updateActiveNoteTitle,
                    onProseChange = viewModel::updateActiveNoteProse,
                    onTogglePreview = viewModel::togglePreviewMode,
                    onToggleZenFocus = viewModel::toggleZenFocusMode,
                    onCycleStatus = viewModel::cycleActiveNoteStatus,
                    onChangeNotebook = viewModel::updateActiveNoteNotebook,
                    onAddTag = viewModel::addActiveNoteTag,
                    onRemoveTag = viewModel::removeActiveNoteTag,
                    onToggleChecklistItem = { lineIdx ->
                        viewModel.toggleNoteChecklistItem(activeNote, lineIdx)
                    },
                    onAddChecklistItem = viewModel::addChecklistItemToActiveNote,
                    onRemoveChecklistItem = viewModel::removeChecklistItemFromActiveNote,
                    onInsertMarkdown = viewModel::insertMarkdownIntoProse,
                    onUndo = viewModel::undo,
                    onRedo = viewModel::redo,
                    onTogglePin = { viewModel.togglePin(activeNote) },
                    onToggleArchive = { viewModel.toggleArchive(activeNote) },
                    onExportPdf = { viewModel.exportNoteAsPdf(activeNote) },
                    onExportScrollImage = { viewModel.exportNoteAsLongScrollImage(activeNote) },
                    onDelete = { viewModel.deleteNote(activeNote) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            } else {
                MainTabScaffold(
                    uiState = uiState,
                    viewModel = viewModel,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            }
        }
    }
}

@Composable
private fun MainTabScaffold(
    uiState: NotesUiState,
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = palette.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ZenBottomNavigationBar(
                activeTab = uiState.activeTab,
                onSelectTab = viewModel::selectTab
            )
        },
        floatingActionButton = {
            if (uiState.activeTab == AppTab.NOTES) {
                FloatingActionButton(
                    onClick = { viewModel.createNewNote() },
                    containerColor = palette.ink,
                    contentColor = palette.background,
                    shape = RoundedCornerShape(14.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("fab_new_note")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "New Thought",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                AppTab.NOTES -> NotesHomeScreen(
                    uiState = uiState,
                    onSearchQueryChange = viewModel::setSearchQuery,
                    onSelectNotebookFilter = viewModel::selectNotebookFilter,
                    onClearTagFilter = { viewModel.selectTagFilter(null) },
                    onSelectTagChip = viewModel::selectTagFilter,
                    onSelectTypeFilter = viewModel::setTypeFilter,
                    onOpenNote = viewModel::openNote,
                    onToggleChecklistItem = viewModel::toggleNoteChecklistItem,
                    onTogglePin = viewModel::togglePin,
                    onToggleArchive = viewModel::toggleArchive,
                    onExportPdf = viewModel::exportNoteAsPdf,
                    onDeleteNote = viewModel::deleteNote,
                    onResetFilters = viewModel::resetHomeFilters
                )

                AppTab.FOLDERS -> TaxonomyScreen(
                    uiState = uiState,
                    onOpenNotebook = viewModel::openNotebookFromTaxonomy,
                    onOpenTag = viewModel::openTagFromTaxonomy,
                    onCreateNotebook = viewModel::createNotebook,
                    onDeleteNotebook = viewModel::deleteNotebook,
                    onAddTag = viewModel::addCustomTag,
                    onRestoreArchivedNote = viewModel::toggleArchive,
                    onDeleteArchivedNote = viewModel::deleteNote
                )

                AppTab.PREFERENCES -> PreferencesScreen(
                    uiState = uiState,
                    onSelectAtmosphere = viewModel::setAtmosphere,
                    onSelectFontVoice = viewModel::setFontVoice,
                    onSelectLineSpacing = viewModel::setLineSpacing,
                    onAdjustFontSize = viewModel::adjustBodyFontSize,
                    onToggleTypewriterCentering = viewModel::setTypewriterCentering,
                    onToggleSubtleWordCount = viewModel::setSubtleWordCount,
                    onToggleHapticFeedback = viewModel::setHapticFeedback,
                    onExportPdf = viewModel::exportNoteAsPdf,
                    onExportMarkdownZip = viewModel::exportAllAsMarkdownZip,
                    onExportLongScrollImage = viewModel::exportNoteAsLongScrollImage
                )
            }
        }
    }
}
