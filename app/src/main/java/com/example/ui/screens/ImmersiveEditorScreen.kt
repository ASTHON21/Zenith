package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.model.Notebook
import com.example.data.preferences.EditorFontVoice
import com.example.ui.components.ZenCheckboxBox
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.LocalZenithPreferences
import com.example.ui.theme.NewsreaderFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImmersiveEditorScreen(
    note: Note,
    notebooks: List<Notebook>,
    isPreviewMode: Boolean,
    zenFocusMode: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    showBackButton: Boolean = true,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onProseChange: (String) -> Unit,
    onTogglePreview: () -> Unit,
    onToggleZenFocus: () -> Unit,
    onCycleStatus: () -> Unit,
    onChangeNotebook: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onToggleChecklistItem: (Int) -> Unit,
    onAddChecklistItem: (String) -> Unit,
    onRemoveChecklistItem: (Int) -> Unit,
    onInsertMarkdown: (String, String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onExportPdf: () -> Unit,
    onExportScrollImage: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    val prefs = LocalZenithPreferences.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    var menuExpanded by remember { mutableStateOf(false) }
    var showNotebookDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var forceShowChecklistCard by remember(note.id) { mutableStateOf(false) }
    var newChecklistInput by remember { mutableStateOf("") }

    val editorFont = if (prefs.fontVoice == EditorFontVoice.SERIF) NewsreaderFontFamily else InterFontFamily
    val bodySizeSp = prefs.bodyFontSizeSp.sp
    val bodyLineHeightSp = (prefs.bodyFontSizeSp * prefs.lineSpacing.multiplier).sp

    val editedTimeStr = remember(note.updatedAt) {
        SimpleDateFormat("HH:mm", Locale.US).format(Date(note.updatedAt))
    }
    val createdDateStr = remember(note.createdAt) {
        SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(note.createdAt))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar (hidden in Zen Focus Mode)
            if (!zenFocusMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.background)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("editor_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = palette.ink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Editor",
                        fontFamily = NewsreaderFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = palette.ink
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            if (prefs.hapticFeedback) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            if (!isPreviewMode) onTogglePreview()
                        },
                        modifier = Modifier.testTag("editor_done_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Finish editing",
                            tint = palette.ink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.testTag("editor_more_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More actions",
                                tint = palette.ink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(palette.cardSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (note.isPinned) "Unpin Reflection" else "Pin as Reflection",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; onTogglePin() }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Move Notebook (${note.notebook})",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; showNotebookDialog = true }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Manage Tags",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; showTagDialog = true }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Zen Focus Mode",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; onToggleZenFocus() }
                            )
                            HorizontalDivider(color = palette.border)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Export as Typeset PDF",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; onExportPdf() }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Share Long Scroll Image",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; onExportScrollImage() }
                            )
                            HorizontalDivider(color = palette.border)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (note.isArchived) "Unarchive Note" else "Archive Note",
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = palette.ink
                                    )
                                },
                                onClick = { menuExpanded = false; onToggleArchive() }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete Note",
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
                HorizontalDivider(thickness = 1.dp, color = palette.border)
            }

            // Central Scrollable Composition Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Metadata Strip
                    if (!zenFocusMode) {
                        val metaSummary = buildString {
                            append("LAST EDITED $editedTimeStr")
                            if (prefs.subtleWordCount) {
                                append(" · ${note.wordCount} WORDS · ~${note.readingTimeMinutes} MIN READ")
                            }
                            append(" · ● Saved")
                        }
                        Text(
                            text = metaSummary,
                            fontFamily = InterFontFamily,
                            fontSize = 10.sp,
                            letterSpacing = 0.7.sp,
                            color = palette.subtleInk
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tags Row
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            note.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(palette.surfaceElevated)
                                        .clickable { showTagDialog = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.mutedInk
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                                    .clickable { showTagDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ Tag",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = palette.subtleInk
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Date · Notebook · Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$createdDateStr · ",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = palette.subtleInk
                            )
                            Text(
                                text = note.notebook,
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.mutedInk,
                                modifier = Modifier.clickable { showNotebookDialog = true }
                            )
                            Text(
                                text = " · ",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = palette.subtleInk
                            )
                            Text(
                                text = note.status,
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.mutedInk,
                                modifier = Modifier.clickable { onCycleStatus() }
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    } else {
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Title Input
                    BasicTextField(
                        value = note.title,
                        onValueChange = {
                            if (prefs.hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTitleChange(it)
                        },
                        readOnly = isPreviewMode,
                        textStyle = TextStyle(
                            fontFamily = NewsreaderFontFamily,
                            fontSize = 26.sp,
                            lineHeight = 33.sp,
                            fontWeight = FontWeight.Normal,
                            color = palette.ink
                        ),
                        cursorBrush = SolidColor(palette.ink),
                        decorationBox = { inner ->
                            if (note.title.isEmpty()) {
                                Text(
                                    text = "Untitled Thought",
                                    fontFamily = NewsreaderFontFamily,
                                    fontSize = 26.sp,
                                    lineHeight = 33.sp,
                                    color = palette.subtleInk
                                )
                            }
                            inner()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("editor_title_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Body Area (Preview Mode vs Active Edit Mode)
                    if (isPreviewMode) {
                        EditorialReaderBody(
                            prose = note.proseContent,
                            fontFamily = editorFont,
                            fontSizeSp = prefs.bodyFontSizeSp,
                            lineHeightMultiplier = prefs.lineSpacing.multiplier
                        )
                    } else {
                        BasicTextField(
                            value = note.proseContent,
                            onValueChange = {
                                if (prefs.hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onProseChange(it)
                            },
                            textStyle = TextStyle(
                                fontFamily = editorFont,
                                fontSize = bodySizeSp,
                                lineHeight = bodyLineHeightSp,
                                color = palette.ink
                            ),
                            cursorBrush = SolidColor(palette.ink),
                            decorationBox = { inner ->
                                if (note.proseContent.isEmpty()) {
                                    Text(
                                        text = "Begin writing here. Negative space is the silent chamber where thoughts take form...",
                                        fontFamily = editorFont,
                                        fontSize = bodySizeSp,
                                        lineHeight = bodyLineHeightSp,
                                        color = palette.subtleInk
                                    )
                                }
                                inner()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("editor_body_input")
                        )
                    }

                    // Embedded Editorial Checklist Card
                    if (note.checklistItems.isNotEmpty() || forceShowChecklistCard) {
                        Spacer(modifier = Modifier.height(28.dp))
                        EditorialChecklistPanel(
                            items = note.checklistItems,
                            newTaskInput = newChecklistInput,
                            onNewTaskInputChange = { newChecklistInput = it },
                            onToggleItem = onToggleChecklistItem,
                            onRemoveItem = onRemoveChecklistItem,
                            onAddItem = {
                                if (newChecklistInput.isNotBlank()) {
                                    onAddChecklistItem(newChecklistInput)
                                    newChecklistInput = ""
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(if (prefs.typewriterCentering) 220.dp else 96.dp))
                }
            }

            // Bottom Docked Formatting Toolbar (hidden in Zen Focus Mode)
            if (!zenFocusMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.background)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .imePadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 520.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ToolbarTextButton(label = "H", fontWeight = FontWeight.Bold) {
                            onInsertMarkdown("## ", "")
                        }
                        ToolbarTextButton(label = "B", fontWeight = FontWeight.Bold) {
                            onInsertMarkdown("**", "**")
                        }
                        ToolbarTextButton(label = "I", fontStyle = FontStyle.Italic) {
                            onInsertMarkdown("*", "*")
                        }
                        ToolbarIconButton(
                            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                            description = "Bullet list"
                        ) {
                            onInsertMarkdown("- ", "")
                        }
                        ToolbarTextButton(label = "”", fontWeight = FontWeight.Bold) {
                            onInsertMarkdown("> ", "")
                        }
                        ToolbarIconButton(
                            icon = Icons.Outlined.CheckBox,
                            description = "Checklist",
                            isActive = note.checklistItems.isNotEmpty() || forceShowChecklistCard
                        ) {
                            forceShowChecklistCard = true
                            if (note.checklistItems.isEmpty()) {
                                onAddChecklistItem("New editorial step")
                            }
                        }
                        ToolbarIconButton(
                            icon = Icons.AutoMirrored.Filled.Undo,
                            description = "Undo",
                            enabled = canUndo,
                            onClick = onUndo
                        )
                        ToolbarIconButton(
                            icon = Icons.AutoMirrored.Filled.Redo,
                            description = "Redo",
                            enabled = canRedo,
                            onClick = onRedo
                        )
                        ToolbarIconButton(
                            icon = if (isPreviewMode) Icons.Filled.Visibility else Icons.Outlined.Visibility,
                            description = "Toggle Preview",
                            isActive = isPreviewMode,
                            onClick = onTogglePreview
                        )
                    }
                }
            }
        }

        // Floating Zen Exit Pill
        if (zenFocusMode) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                    .clickable { onToggleZenFocus() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("exit_zen_focus_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = "Exit Zen Mode",
                    tint = palette.mutedInk,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exit Zen",
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = palette.mutedInk
                )
            }
        }
    }

    // Move Notebook Dialog
    if (showNotebookDialog) {
        AlertDialog(
            onDismissRequest = { showNotebookDialog = false },
            containerColor = palette.cardSurface,
            title = {
                Text(
                    text = "Select Notebook",
                    fontFamily = NewsreaderFontFamily,
                    fontSize = 20.sp,
                    color = palette.ink
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (notebooks.isEmpty()) {
                        Text(
                            text = "No notebooks created yet. You can create notebooks in the Folders tab.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = palette.mutedInk
                        )
                    } else {
                        val unfiledSelected = note.notebook.equals("Unfiled", ignoreCase = true) || note.notebook.isBlank()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (unfiledSelected) palette.surfaceElevated else Color.Transparent)
                                .border(1.dp, if (unfiledSelected) palette.ink else palette.border, RoundedCornerShape(8.dp))
                                .clickable {
                                    onChangeNotebook("Unfiled")
                                    showNotebookDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Unfiled", fontFamily = NewsreaderFontFamily, fontSize = 15.sp, color = palette.ink)
                                Text("No specific notebook", fontFamily = InterFontFamily, fontSize = 11.sp, color = palette.subtleInk)
                            }
                            if (unfiledSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = palette.ink, modifier = Modifier.size(16.dp))
                            }
                        }
                        notebooks.forEach { nb ->
                            val selected = nb.name.equals(note.notebook, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) palette.surfaceElevated else Color.Transparent)
                                    .border(1.dp, if (selected) palette.ink else palette.border, RoundedCornerShape(8.dp))
                                    .clickable {
                                        onChangeNotebook(nb.name)
                                        showNotebookDialog = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(nb.name, fontFamily = NewsreaderFontFamily, fontSize = 15.sp, color = palette.ink)
                                    Text(nb.subtitle, fontFamily = InterFontFamily, fontSize = 11.sp, color = palette.subtleInk)
                                }
                                if (selected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = palette.ink, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotebookDialog = false }) {
                    Text("Close", fontFamily = InterFontFamily, color = palette.ink)
                }
            }
        )
    }

    // Manage Tags Dialog
    if (showTagDialog) {
        var tagInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            containerColor = palette.cardSurface,
            title = {
                Text("Manage Tags", fontFamily = NewsreaderFontFamily, fontSize = 20.sp, color = palette.ink)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        note.tags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.surfaceElevated)
                                    .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#$tag", fontFamily = InterFontFamily, fontSize = 12.sp, color = palette.ink)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove $tag",
                                    tint = palette.subtleInk,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onRemoveTag(tag) }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it },
                            singleLine = true,
                            textStyle = TextStyle(fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink),
                            cursorBrush = SolidColor(palette.ink),
                            decorationBox = { inner ->
                                if (tagInput.isEmpty()) {
                                    Text("New tag name...", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.subtleInk)
                                }
                                inner()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add tag",
                            tint = palette.ink,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    if (tagInput.isNotBlank()) {
                                        onAddTag(tagInput)
                                        tagInput = ""
                                    }
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Done", fontFamily = InterFontFamily, color = palette.ink)
                }
            }
        )
    }
}

@Composable
private fun EditorialReaderBody(
    prose: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    fontSizeSp: Int,
    lineHeightMultiplier: Float
) {
    val palette = LocalZenithPalette.current
    val bodySize = fontSizeSp.sp
    val bodyLineHeight = (fontSizeSp * lineHeightMultiplier).sp

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        prose.lines().forEach { rawLine ->
            val trimmed = rawLine.trim()
            when {
                trimmed.isEmpty() -> Spacer(modifier = Modifier.height(6.dp))
                trimmed.startsWith("### ") -> Text(
                    text = trimmed.removePrefix("### "),
                    fontFamily = NewsreaderFontFamily,
                    fontSize = (fontSizeSp + 3).sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.ink,
                    modifier = Modifier.padding(top = 8.dp)
                )
                trimmed.startsWith("## ") -> Text(
                    text = trimmed.removePrefix("## "),
                    fontFamily = NewsreaderFontFamily,
                    fontSize = (fontSizeSp + 5).sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.ink,
                    modifier = Modifier.padding(top = 10.dp)
                )
                trimmed.startsWith("# ") -> Text(
                    text = trimmed.removePrefix("# "),
                    fontFamily = NewsreaderFontFamily,
                    fontSize = (fontSizeSp + 7).sp,
                    fontWeight = FontWeight.Normal,
                    color = palette.ink,
                    modifier = Modifier.padding(top = 12.dp)
                )
                trimmed.startsWith("> ") -> {
                    val quoteContent = trimmed.removePrefix("> ")
                    val parts = quoteContent.split(" — ", limit = 2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(72.dp)
                                    .background(palette.ink)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = parts[0],
                                    fontFamily = NewsreaderFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = bodySize,
                                    lineHeight = bodyLineHeight,
                                    color = palette.ink
                                )
                                if (parts.size > 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "— ${parts[1]}",
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.subtleInk
                                    )
                                }
                            }
                        }
                    }
                }
                trimmed.startsWith("- ") -> Row(verticalAlignment = Alignment.Top) {
                    Text("•", fontFamily = fontFamily, fontSize = bodySize, color = palette.mutedInk, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = trimmed.removePrefix("- "),
                        fontFamily = fontFamily,
                        fontSize = bodySize,
                        lineHeight = bodyLineHeight,
                        color = palette.ink
                    )
                }
                else -> Text(
                    text = trimmed.replace("**", "").replace("*", ""),
                    fontFamily = fontFamily,
                    fontSize = bodySize,
                    lineHeight = bodyLineHeight,
                    color = palette.ink
                )
            }
        }
    }
}

@Composable
private fun EditorialChecklistPanel(
    items: List<com.example.data.model.ChecklistItem>,
    newTaskInput: String,
    onNewTaskInputChange: (String) -> Unit,
    onToggleItem: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onAddItem: () -> Unit
) {
    val palette = LocalZenithPalette.current
    val completedCount = items.count { it.isChecked }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .padding(16.dp)
            .testTag("editor_checklist_panel")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = palette.ink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editorial Checklist · Publishing Notes",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.ink
                )
            }
            Text(
                text = "$completedCount/${items.size} completed",
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                color = palette.subtleInk
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleItem(item.lineIndex) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZenCheckboxBox(
                        isChecked = item.isChecked,
                        onToggle = { onToggleItem(item.lineIndex) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item.text,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isChecked) palette.subtleInk else palette.ink,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove task",
                        tint = palette.subtleInk,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onRemoveItem(item.lineIndex) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = palette.border)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = newTaskInput,
                onValueChange = onNewTaskInputChange,
                singleLine = true,
                textStyle = TextStyle(fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink),
                cursorBrush = SolidColor(palette.ink),
                decorationBox = { inner ->
                    if (newTaskInput.isEmpty()) {
                        Text(
                            text = "Add checklist item...",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = palette.subtleInk
                        )
                    }
                    inner()
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("editor_add_checklist_input")
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.ink)
                    .clickable { onAddItem() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("editor_add_checklist_button")
            ) {
                Text(
                    text = "Add",
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.background
                )
            }
        }
    }
}

@Composable
private fun ToolbarTextButton(
    label: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    onClick: () -> Unit
) {
    val palette = LocalZenithPalette.current
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = NewsreaderFontFamily,
            fontSize = 15.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            color = palette.ink
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    isActive: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val palette = LocalZenithPalette.current
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) palette.surfaceElevated else Color.Transparent)
            .alpha(if (enabled) 1f else 0.3f)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (isActive) palette.ink else palette.mutedInk,
            modifier = Modifier.size(18.dp)
        )
    }
}
