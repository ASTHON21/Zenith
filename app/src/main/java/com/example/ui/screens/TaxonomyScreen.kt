package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.model.Notebook
import com.example.ui.components.ZenScreenHeader
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.viewmodel.NotebookSummary
import com.example.ui.viewmodel.NotesUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaxonomyScreen(
    uiState: NotesUiState,
    onOpenNotebook: (String) -> Unit,
    onOpenTag: (String) -> Unit,
    onCreateNotebook: (name: String, subtitle: String) -> Unit,
    onDeleteNotebook: (Notebook) -> Unit,
    onAddTag: (String) -> Unit,
    onRestoreArchivedNote: (Note) -> Unit,
    onDeleteArchivedNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    var showCreateNotebookDialog by remember { mutableStateOf(false) }
    var showCreateTagDialog by remember { mutableStateOf(false) }
    var showArchivedDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        ZenScreenHeader(rightSectionLabel = "TAXONOMY")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Strip Card
            item(key = "taxonomy_summary") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.cardSurface)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = null,
                            tint = palette.subtleInk,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${uiState.totalActiveNotesCount} notes · ${uiState.notebookSummaries.size} notebooks · ${uiState.tagSummaries.size} tags",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = palette.mutedInk
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showCreateNotebookDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("new_notebook_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = palette.ink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Notebook",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.ink
                        )
                    }
                }
            }

            // Notebooks Header
            item(key = "notebooks_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notebooks",
                        fontFamily = NewsreaderFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = palette.ink
                    )
                    Text(
                        text = "ARCHIVES",
                        fontFamily = InterFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.2.sp,
                        color = palette.subtleInk
                    )
                }
            }

            // Numbered Notebook Cards or Empty State
            if (uiState.notebookSummaries.isEmpty()) {
                item(key = "empty_notebooks") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .clickable { showCreateNotebookDialog = true }
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                            .testTag("empty_notebooks_card"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No notebooks yet",
                            fontFamily = NewsreaderFontFamily,
                            fontSize = 16.sp,
                            color = palette.ink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + New Notebook to create your first collection",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = palette.subtleInk,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = uiState.notebookSummaries,
                    key = { "nb_${it.notebook.id}" }
                ) { summary ->
                    NotebookCard(
                        index = summary.notebook.orderIndex,
                        summary = summary,
                        canDelete = true,
                        onClick = { onOpenNotebook(summary.notebook.name) },
                        onDelete = { onDeleteNotebook(summary.notebook) }
                    )
                }
            }

            // Archived Notes Entry (shown when archived notes exist)
            if (uiState.archivedNotes.isNotEmpty()) {
                item(key = "archived_notes_entry") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .clickable { showArchivedDialog = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("archived_notes_entry"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = null,
                            tint = palette.mutedInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Archived Notes",
                                fontFamily = NewsreaderFontFamily,
                                fontSize = 15.sp,
                                color = palette.ink
                            )
                            Text(
                                text = "Tap to inspect, restore, or remove",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = palette.subtleInk
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.cardSurface)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${uiState.archivedNotes.size}",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = palette.mutedInk
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open archived notes",
                            tint = palette.subtleInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Tags Cloud Section
            item(key = "tags_cloud") {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tags Cloud",
                            fontFamily = NewsreaderFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = palette.ink
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showCreateTagDialog = true }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("add_taxonomy_tag_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = palette.mutedInk,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Tag",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = palette.mutedInk
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.tagSummaries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.cardSurface)
                                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                .clickable { showCreateTagDialog = true }
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                                .testTag("empty_tags_card")
                        ) {
                            Text(
                                text = "No tags yet. Tap + Add Tag or tag any note in the editor.",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = palette.subtleInk
                            )
                        }
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.tagSummaries.forEach { tagItem ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.cardSurface)
                                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                        .clickable { onOpenTag(tagItem.tag) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("taxonomy_tag_${tagItem.tag}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${tagItem.tag}",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = palette.ink
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${tagItem.count}",
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.subtleInk
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Philosophy Quote Card
            item(key = "archive_philosophy_quote") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceElevated)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(1.dp)
                            .background(palette.subtleInk)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "\"To observe things as they are; taxonomy is clarity.\"",
                        fontFamily = NewsreaderFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = palette.ink,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ZENITH ARCHIVE PHILOSOPHY",
                        fontFamily = InterFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = palette.subtleInk,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Create Notebook Dialog
    if (showCreateNotebookDialog) {
        var nbName by remember { mutableStateOf("") }
        var nbSubtitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateNotebookDialog = false },
            containerColor = palette.cardSurface,
            title = {
                Text("New Notebook", fontFamily = NewsreaderFontFamily, fontSize = 20.sp, color = palette.ink)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ZenDialogInputField(
                        value = nbName,
                        onValueChange = { nbName = it },
                        placeholder = "Notebook title (e.g. Field Studies)"
                    )
                    ZenDialogInputField(
                        value = nbSubtitle,
                        onValueChange = { nbSubtitle = it },
                        placeholder = "Subtitle (e.g. Observations & Sketches)"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nbName.isNotBlank()) {
                            onCreateNotebook(nbName, nbSubtitle)
                            showCreateNotebookDialog = false
                        }
                    }
                ) {
                    Text("Create", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = palette.ink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateNotebookDialog = false }) {
                    Text("Cancel", fontFamily = InterFontFamily, color = palette.subtleInk)
                }
            }
        )
    }

    // Add Tag Dialog
    if (showCreateTagDialog) {
        var newTag by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateTagDialog = false },
            containerColor = palette.cardSurface,
            title = {
                Text("Add Taxonomy Tag", fontFamily = NewsreaderFontFamily, fontSize = 20.sp, color = palette.ink)
            },
            text = {
                ZenDialogInputField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    placeholder = "Tag name (e.g. Aesthetics)"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTag.isNotBlank()) {
                            onAddTag(newTag)
                            showCreateTagDialog = false
                        }
                    }
                ) {
                    Text("Add", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = palette.ink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTagDialog = false }) {
                    Text("Cancel", fontFamily = InterFontFamily, color = palette.subtleInk)
                }
            }
        )
    }

    // Archived Notes Dialog
    if (showArchivedDialog) {
        AlertDialog(
            onDismissRequest = { showArchivedDialog = false },
            containerColor = palette.cardSurface,
            title = {
                Text("Archived Notes", fontFamily = NewsreaderFontFamily, fontSize = 20.sp, color = palette.ink)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.archivedNotes.isEmpty()) {
                        Text(
                            text = "No archived notes.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = palette.mutedInk
                        )
                    } else {
                        uiState.archivedNotes.forEach { archived ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.surfaceElevated)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = archived.title.ifBlank { "Untitled Thought" },
                                        fontFamily = NewsreaderFontFamily,
                                        fontSize = 14.sp,
                                        color = palette.ink
                                    )
                                    Text(
                                        text = archived.notebook,
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.subtleInk
                                    )
                                }
                                TextButton(onClick = { onRestoreArchivedNote(archived) }) {
                                    Text("Restore", fontFamily = InterFontFamily, fontSize = 11.sp, color = palette.ink)
                                }
                                TextButton(onClick = { onDeleteArchivedNote(archived) }) {
                                    Text("Delete", fontFamily = InterFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showArchivedDialog = false }) {
                    Text("Close", fontFamily = InterFontFamily, color = palette.ink)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotebookCard(
    index: Int,
    summary: NotebookSummary,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = LocalZenithPalette.current
    val indexStr = String.format(Locale.US, "%02d", index)
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { if (canDelete) menuExpanded = true }
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .testTag("notebook_card_${summary.notebook.name}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = indexStr,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                color = palette.subtleInk,
                modifier = Modifier.width(26.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.notebook.name,
                    fontFamily = NewsreaderFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.ink
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = summary.notebook.subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = palette.subtleInk
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${summary.noteCount}",
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = palette.mutedInk
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open notebook",
                tint = palette.subtleInk,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.background(palette.cardSurface)
        ) {
            DropdownMenuItem(
                text = { Text("Open Notebook", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                onClick = { menuExpanded = false; onClick() }
            )
            if (canDelete) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Delete Notebook",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = { menuExpanded = false; onDelete() }
                )
            }
        }
    }
}

@Composable
private fun ZenDialogInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val palette = LocalZenithPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                color = palette.subtleInk
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink),
            cursorBrush = SolidColor(palette.ink),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
