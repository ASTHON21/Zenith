package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.model.TemporalBucket
import com.example.ui.components.ZenCheckboxBox
import com.example.ui.components.ZenScreenHeader
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.LocalZenithPreferences
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.viewmodel.NoteTypeFilter
import com.example.ui.viewmodel.NotesUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NotesHomeScreen(
    uiState: NotesUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectNotebookFilter: (String) -> Unit,
    onClearTagFilter: () -> Unit,
    onSelectTagChip: (String) -> Unit,
    onSelectTypeFilter: (NoteTypeFilter) -> Unit,
    onOpenNote: (Note) -> Unit,
    onToggleChecklistItem: (Note, Int) -> Unit,
    onTogglePin: (Note) -> Unit,
    onToggleArchive: (Note) -> Unit,
    onExportPdf: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    val prefs = LocalZenithPreferences.current
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val todaySubtitle = remember {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val season = when (month) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> "Winter"
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> "Spring"
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> "Summer"
            else -> "Autumn"
        }
        val dateText = SimpleDateFormat("MMMM d", Locale.US).format(cal.time)
        "$dateText · $season"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        ZenScreenHeader(rightSectionLabel = "NOTES")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Search Bar
            item(key = "search_bar") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = palette.subtleInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search thoughts, clippings, or drafts...",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = palette.subtleInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BasicTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = palette.ink
                                ),
                                cursorBrush = SolidColor(palette.ink),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("home_search_input")
                            )
                        }
                        if (uiState.searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = palette.subtleInk,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onSearchQueryChange("") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Box {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter formats",
                                tint = if (uiState.typeFilter == NoteTypeFilter.ALL) palette.subtleInk else palette.ink,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { filterMenuExpanded = true }
                                    .testTag("home_filter_button")
                            )
                            DropdownMenu(
                                expanded = filterMenuExpanded,
                                onDismissRequest = { filterMenuExpanded = false },
                                modifier = Modifier.background(palette.cardSurface)
                            ) {
                                NoteTypeFilter.entries.forEach { filter ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (filter == uiState.typeFilter) "✓ ${filter.label}" else filter.label,
                                                fontFamily = InterFontFamily,
                                                fontSize = 13.sp,
                                                color = palette.ink
                                            )
                                        },
                                        onClick = {
                                            filterMenuExpanded = false
                                            onSelectTypeFilter(filter)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notebook & Active Tag Filter Pills (only shown when notebooks or active tag filter exist)
            if (uiState.notebooks.isNotEmpty() || uiState.selectedTag != null) {
                item(key = "filter_pills") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val pillNames = listOf("All") + uiState.notebooks.map { it.name }
                        pillNames.forEach { name ->
                            val isSelected = uiState.selectedNotebook.equals(name, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) palette.ink else palette.surfaceElevated)
                                    .clickable { onSelectNotebookFilter(name) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                    .testTag("home_pill_$name")
                            ) {
                                Text(
                                    text = name,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (isSelected) palette.background else palette.mutedInk
                                )
                            }
                        }

                        if (uiState.selectedTag != null) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, palette.ink, RoundedCornerShape(16.dp))
                                    .background(palette.cardSurface)
                                    .clickable { onClearTagFilter() }
                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${uiState.selectedTag}",
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.ink
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear tag filter",
                                    tint = palette.ink,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Pinned Reflection Hero Card
            if (uiState.pinnedReflection != null) {
                item(key = "pinned_reflection_${uiState.pinnedReflection.id}") {
                    PinnedReflectionCard(
                        note = uiState.pinnedReflection,
                        showWordCount = prefs.subtleWordCount,
                        onOpen = { onOpenNote(uiState.pinnedReflection) },
                        onUnpin = { onTogglePin(uiState.pinnedReflection) },
                        onExportPdf = { onExportPdf(uiState.pinnedReflection) },
                        onDelete = { onDeleteNote(uiState.pinnedReflection) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // Temporal Sections
            if (uiState.todayNotes.isNotEmpty()) {
                item(key = "header_today") {
                    TemporalSectionHeader(
                        label = "TODAY",
                        subtitle = todaySubtitle,
                        count = uiState.todayNotes.size
                    )
                }
                items(
                    items = uiState.todayNotes,
                    key = { "today_${it.id}" }
                ) { note ->
                    HomeNoteCard(
                        note = note,
                        showWordCount = prefs.subtleWordCount,
                        onOpen = { onOpenNote(note) },
                        onToggleChecklist = { idx -> onToggleChecklistItem(note, idx) },
                        onTagClick = onSelectTagChip,
                        onTogglePin = { onTogglePin(note) },
                        onToggleArchive = { onToggleArchive(note) },
                        onExportPdf = { onExportPdf(note) },
                        onDelete = { onDeleteNote(note) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            if (uiState.thisWeekNotes.isNotEmpty()) {
                item(key = "header_week") {
                    TemporalSectionHeader(
                        label = "THIS WEEK",
                        subtitle = null,
                        count = uiState.thisWeekNotes.size
                    )
                }
                items(
                    items = uiState.thisWeekNotes,
                    key = { "week_${it.id}" }
                ) { note ->
                    HomeNoteCard(
                        note = note,
                        showWordCount = prefs.subtleWordCount,
                        onOpen = { onOpenNote(note) },
                        onToggleChecklist = { idx -> onToggleChecklistItem(note, idx) },
                        onTagClick = onSelectTagChip,
                        onTogglePin = { onTogglePin(note) },
                        onToggleArchive = { onToggleArchive(note) },
                        onExportPdf = { onExportPdf(note) },
                        onDelete = { onDeleteNote(note) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            if (uiState.earlierNotes.isNotEmpty()) {
                item(key = "header_earlier") {
                    TemporalSectionHeader(
                        label = "EARLIER",
                        subtitle = null,
                        count = uiState.earlierNotes.size
                    )
                }
                items(
                    items = uiState.earlierNotes,
                    key = { "earlier_${it.id}" }
                ) { note ->
                    HomeNoteCard(
                        note = note,
                        showWordCount = prefs.subtleWordCount,
                        onOpen = { onOpenNote(note) },
                        onToggleChecklist = { idx -> onToggleChecklistItem(note, idx) },
                        onTagClick = onSelectTagChip,
                        onTogglePin = { onTogglePin(note) },
                        onToggleArchive = { onToggleArchive(note) },
                        onExportPdf = { onExportPdf(note) },
                        onDelete = { onDeleteNote(note) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // Empty State
            if (uiState.pinnedReflection == null &&
                uiState.todayNotes.isEmpty() &&
                uiState.thisWeekNotes.isEmpty() &&
                uiState.earlierNotes.isEmpty()
            ) {
                item(key = "empty_home") {
                    val hasFilters = uiState.searchQuery.isNotBlank() ||
                        uiState.selectedNotebook != "All" ||
                        uiState.selectedTag != null ||
                        uiState.typeFilter != NoteTypeFilter.ALL

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ma (間)",
                            fontFamily = NewsreaderFontFamily,
                            fontSize = 24.sp,
                            color = palette.subtleInk
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hasFilters) {
                                "No thoughts match the active filters."
                            } else {
                                "The page awaits your contemplation. Tap the pen icon to begin."
                            },
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = palette.mutedInk,
                            textAlign = TextAlign.Center
                        )
                        if (hasFilters) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .background(palette.cardSurface)
                                    .clickable { onResetFilters() }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("reset_filters_button")
                            ) {
                                Text(
                                    text = "Reset Filters",
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.ink
                                )
                            }
                        }
                    }
                }
            }

            // Editorial Colophon Footer when notes are present
            val hasVisibleNotes = uiState.pinnedReflection != null ||
                uiState.todayNotes.isNotEmpty() ||
                uiState.thisWeekNotes.isNotEmpty() ||
                uiState.earlierNotes.isNotEmpty()
            if (hasVisibleNotes) {
                item(key = "colophon_footer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(1.dp)
                                .background(palette.border)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Silence & Stillness — Between paper white and ink black, giving form to thoughts.",
                            fontFamily = NewsreaderFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = palette.subtleInk,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedReflectionCard(
    note: Note,
    showWordCount: Boolean,
    onOpen: () -> Unit,
    onUnpin: () -> Unit,
    onExportPdf: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    var menuExpanded by remember { mutableStateOf(false) }

    val elapsedText = remember(note.updatedAt) {
        val diffMins = ((System.currentTimeMillis() - note.updatedAt) / 60_000L).coerceAtLeast(1)
        when {
            diffMins < 60 -> "${diffMins}M AGO"
            diffMins < 1440 -> "${diffMins / 60}H AGO"
            else -> "${diffMins / 1440}D AGO"
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .clickable { onOpen() }
            .padding(18.dp)
            .testTag("pinned_reflection_card")
    ) {
        // Subtle editorial quote watermark
        Text(
            text = "”",
            fontFamily = NewsreaderFontFamily,
            fontSize = 72.sp,
            color = palette.subtleInk,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .alpha(0.12f)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = palette.subtleInk,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PINNED REFLECTION · $elapsedText",
                    fontFamily = InterFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp,
                    color = palette.subtleInk,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Reflection options",
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
                            text = { Text("Open in Editor", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                            onClick = { menuExpanded = false; onOpen() }
                        )
                        DropdownMenuItem(
                            text = { Text("Unpin Reflection", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                            onClick = { menuExpanded = false; onUnpin() }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Typeset PDF", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                            onClick = { menuExpanded = false; onExportPdf() }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", fontFamily = InterFontFamily, fontSize = 13.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.snippet.let { if (it.startsWith("\"")) it else "\"$it\"" },
                fontFamily = NewsreaderFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 17.sp,
                lineHeight = 26.sp,
                color = palette.ink,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${note.title.ifBlank { "Untitled" }} · ${note.status}",
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = palette.mutedInk
                )
                if (showWordCount) {
                    Text(
                        text = "${note.wordCount} words",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = palette.subtleInk
                    )
                }
            }
        }
    }
}

@Composable
private fun TemporalSectionHeader(
    label: String,
    subtitle: String?,
    count: Int
) {
    val palette = LocalZenithPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp,
                color = palette.ink
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = palette.subtleInk
                )
            }
        }
        Text(
            text = if (count == 1) "1 note" else "$count notes",
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            color = palette.subtleInk
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeNoteCard(
    note: Note,
    showWordCount: Boolean,
    onOpen: () -> Unit,
    onToggleChecklist: (Int) -> Unit,
    onTagClick: (String) -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onExportPdf: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    var cardMenuExpanded by remember { mutableStateOf(false) }

    val formattedTime = remember(note.updatedAt, note.temporalBucket) {
        when (note.temporalBucket) {
            TemporalBucket.TODAY -> SimpleDateFormat("HH:mm", Locale.US).format(Date(note.updatedAt))
            TemporalBucket.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                val noteCal = Calendar.getInstance().apply { timeInMillis = note.updatedAt }
                if (cal.get(Calendar.DAY_OF_YEAR) - noteCal.get(Calendar.DAY_OF_YEAR) == 1) {
                    "Yesterday"
                } else {
                    SimpleDateFormat("EEEE", Locale.US).format(Date(note.updatedAt))
                }
            }
            TemporalBucket.EARLIER -> SimpleDateFormat("MMM d", Locale.US).format(Date(note.updatedAt))
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = { onOpen() },
                    onLongClick = { cardMenuExpanded = true }
                )
                .padding(16.dp)
                .testTag("home_note_card_${note.id}")
        ) {
        // Card Title & Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = note.title.ifBlank { "Untitled Thought" },
                fontFamily = NewsreaderFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 23.sp,
                color = palette.ink,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
            Text(
                text = formattedTime,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                color = palette.subtleInk
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card Body: Checklist / Quote Excerpt / Standard Preview
        if (note.isChecklistFocused) {
            val previewTasks = note.checklistItems.take(3)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                previewTasks.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChecklist(item.lineIndex) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ZenCheckboxBox(
                            isChecked = item.isChecked,
                            onToggle = { onToggleChecklist(item.lineIndex) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.text,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (item.isChecked) palette.subtleInk else palette.ink,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } else if (note.isQuoteFocused) {
            Text(
                text = note.snippet,
                fontFamily = NewsreaderFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = palette.mutedInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Text(
                text = note.snippet,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = palette.mutedInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Footer: Tag pill + completion/word metric
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val primaryTag = note.tags.firstOrNull()
            if (primaryTag != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.surfaceElevated)
                        .clickable { onTagClick(primaryTag) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "#$primaryTag",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = palette.mutedInk
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            val metricText = when {
                note.isChecklistFocused -> {
                    val done = note.checklistItems.count { it.isChecked }
                    val total = note.checklistItems.size
                    "$done/$total completed"
                }
                note.isQuoteFocused && note.notebook.equals("Reading Excerpts", ignoreCase = true) -> {
                    val excerptsCount = note.proseContent.split("\n\n").count { it.isNotBlank() }.coerceAtLeast(1)
                    "$excerptsCount excerpts"
                }
                showWordCount -> "${note.wordCount} words"
                else -> ""
            }

            if (metricText.isNotEmpty()) {
                Text(
                    text = metricText,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = palette.subtleInk
                )
            }
        }
        }

        DropdownMenu(
            expanded = cardMenuExpanded,
            onDismissRequest = { cardMenuExpanded = false },
            modifier = Modifier.background(palette.cardSurface)
        ) {
            DropdownMenuItem(
                text = { Text("Open in Editor", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                onClick = { cardMenuExpanded = false; onOpen() }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (note.isPinned) "Unpin Reflection" else "Pin as Reflection",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = palette.ink
                    )
                },
                onClick = { cardMenuExpanded = false; onTogglePin() }
            )
            DropdownMenuItem(
                text = { Text("Export Typeset PDF", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                onClick = { cardMenuExpanded = false; onExportPdf() }
            )
            DropdownMenuItem(
                text = { Text("Archive Note", fontFamily = InterFontFamily, fontSize = 13.sp, color = palette.ink) },
                onClick = { cardMenuExpanded = false; onToggleArchive() }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        "Delete Note",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                },
                onClick = { cardMenuExpanded = false; onDelete() }
            )
        }
    }
}
