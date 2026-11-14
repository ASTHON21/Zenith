package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.preferences.AtmosphereTone
import com.example.data.preferences.EditorFontVoice
import com.example.data.preferences.LineSpacingMode
import com.example.ui.components.ZenScreenHeader
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.LocalZenithPreferences
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.viewmodel.NotesUiState

private enum class SingleNoteExportMode {
    PDF,
    LONG_IMAGE
}

@Composable
fun PreferencesScreen(
    uiState: NotesUiState,
    onSelectAtmosphere: (AtmosphereTone) -> Unit,
    onSelectFontVoice: (EditorFontVoice) -> Unit,
    onSelectLineSpacing: (LineSpacingMode) -> Unit,
    onAdjustFontSize: (Int) -> Unit,
    onToggleTypewriterCentering: (Boolean) -> Unit,
    onToggleSubtleWordCount: (Boolean) -> Unit,
    onToggleHapticFeedback: (Boolean) -> Unit,
    onExportPdf: (Note) -> Unit,
    onExportMarkdownZip: () -> Unit,
    onExportLongScrollImage: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    val prefs = LocalZenithPreferences.current
    val haptic = LocalHapticFeedback.current

    var exportPickerMode by remember { mutableStateOf<SingleNoteExportMode?>(null) }

    val previewFont = if (prefs.fontVoice == EditorFontVoice.SERIF) NewsreaderFontFamily else InterFontFamily
    val previewBodySize = prefs.bodyFontSizeSp.sp
    val previewLineHeight = (prefs.bodyFontSizeSp * prefs.lineSpacing.multiplier).sp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        ZenScreenHeader(rightSectionLabel = "PREFERENCES")

        if (uiState.isExporting) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = palette.ink,
                trackColor = palette.border
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Live Typography Preview
            item(key = "live_preview") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE TYPOGRAPHY PREVIEW",
                            fontFamily = InterFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.0.sp,
                            color = palette.subtleInk
                        )
                        Text(
                            text = "${prefs.atmosphere.displayName} · ${prefs.bodyFontSizeSp}pt",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = palette.subtleInk
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val previewNote = uiState.notes.firstOrNull {
                        it.title.isNotBlank() || it.proseContent.isNotBlank()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                            .testTag("live_typography_preview_card")
                    ) {
                        if (previewNote != null) {
                            Text(
                                text = previewNote.title.ifBlank { "UNTITLED THOUGHT" }.uppercase(),
                                fontFamily = InterFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.8.sp,
                                color = palette.subtleInk,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = previewNote.proseContent.ifBlank { previewNote.snippet },
                                fontFamily = previewFont,
                                fontSize = previewBodySize,
                                lineHeight = previewLineHeight,
                                color = palette.ink,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "TYPOGRAPHY SPECIMEN",
                                fontFamily = InterFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.8.sp,
                                color = palette.subtleInk
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Adjust font family, line spacing, and body size below to preview how your writing appears across the editor and exported pages.",
                                fontFamily = previewFont,
                                fontSize = previewBodySize,
                                lineHeight = previewLineHeight,
                                color = palette.ink
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your latest note will appear here automatically once created.",
                                fontFamily = previewFont,
                                fontSize = (prefs.bodyFontSizeSp - 2).coerceAtLeast(11).sp,
                                lineHeight = ((prefs.bodyFontSizeSp - 2).coerceAtLeast(11) * prefs.lineSpacing.multiplier).sp,
                                color = palette.mutedInk
                            )
                        }
                    }
                }
            }

            // Atmosphere & Tone
            item(key = "atmosphere_tone") {
                Column {
                    SectionHeading(title = "Atmosphere & Tone", subtitle = "Texture & Light Balance")
                    Spacer(modifier = Modifier.height(12.dp))

                    val tones = AtmosphereTone.entries
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (rowIndex in 0 until 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (colIndex in 0 until 2) {
                                    val tone = tones[rowIndex * 2 + colIndex]
                                    val isSelected = prefs.atmosphere == tone
                                    AtmosphereTile(
                                        tone = tone,
                                        isSelected = isSelected,
                                        onClick = {
                                            if (prefs.hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onSelectAtmosphere(tone)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Typography & Rhythm
            item(key = "typography_rhythm") {
                Column {
                    SectionHeading(title = "Typography & Rhythm", subtitle = "Pacing & Line Scale")
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Font Family Segmented Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Font Family",
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.ink
                            )
                            SegmentedControl(
                                options = EditorFontVoice.entries.map { it.label },
                                selectedIndex = EditorFontVoice.entries.indexOf(prefs.fontVoice),
                                onSelect = { index ->
                                    if (prefs.hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectFontVoice(EditorFontVoice.entries[index])
                                }
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = palette.border)

                        // Line Spacing Segmented Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Line Spacing",
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.ink
                            )
                            SegmentedControl(
                                options = LineSpacingMode.entries.map { it.label },
                                selectedIndex = LineSpacingMode.entries.indexOf(prefs.lineSpacing),
                                onSelect = { index ->
                                    if (prefs.hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectLineSpacing(LineSpacingMode.entries[index])
                                }
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = palette.border)

                        // Body Font Size Stepper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Body Font Size",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.ink
                                )
                                Text(
                                    text = "Fine-tune visual center",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = palette.subtleInk
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.surfaceElevated)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onAdjustFontSize(-1) }
                                        .testTag("font_size_decrease"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("−", fontFamily = InterFontFamily, fontSize = 16.sp, color = palette.ink)
                                }
                                Text(
                                    text = "${prefs.bodyFontSizeSp}",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.ink,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onAdjustFontSize(1) }
                                        .testTag("font_size_increase"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontFamily = InterFontFamily, fontSize = 16.sp, color = palette.ink)
                                }
                            }
                        }
                    }
                }
            }

            // Focus & Flow
            item(key = "focus_flow") {
                Column {
                    SectionHeading(title = "Focus & Flow", subtitle = "Eliminate Audio-Visual Noise")
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        FocusToggleRow(
                            title = "Typewriter Centering",
                            subtitle = "Keep active typing line locked to viewport center",
                            checked = prefs.typewriterCentering,
                            onCheckedChange = onToggleTypewriterCentering
                        )
                        HorizontalDivider(thickness = 1.dp, color = palette.border)
                        FocusToggleRow(
                            title = "Subtle Word Count",
                            subtitle = "Display count gently in bottom corner without distraction",
                            checked = prefs.subtleWordCount,
                            onCheckedChange = onToggleSubtleWordCount
                        )
                        HorizontalDivider(thickness = 1.dp, color = palette.border)
                        FocusToggleRow(
                            title = "Haptic Feedback",
                            subtitle = "Subtle mechanical keypress sensations",
                            checked = prefs.hapticFeedback,
                            onCheckedChange = onToggleHapticFeedback
                        )
                    }
                }
            }

            // Export & Preservation
            item(key = "export_preservation") {
                Column {
                    SectionHeading(title = "Export & Preservation", subtitle = "Lossless offline formats")
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    ) {
                        ExportActionRow(
                            icon = Icons.Outlined.Description,
                            title = "Export as Typeset PDF",
                            subtitle = "Book-grade editorial layout with custom fonts",
                            onClick = { exportPickerMode = SingleNoteExportMode.PDF },
                            testTag = "export_pdf_row"
                        )
                        HorizontalDivider(thickness = 1.dp, color = palette.border)
                        ExportActionRow(
                            icon = Icons.Outlined.FolderZip,
                            title = "Markdown Package (.zip)",
                            subtitle = "Pure plain-text files organized by notebook",
                            onClick = onExportMarkdownZip,
                            testTag = "export_zip_row"
                        )
                        HorizontalDivider(thickness = 1.dp, color = palette.border)
                        ExportActionRow(
                            icon = Icons.Outlined.Image,
                            title = "Shareable Long Scroll Image",
                            subtitle = "High-resolution ink scroll suited for reading",
                            onClick = { exportPickerMode = SingleNoteExportMode.LONG_IMAGE },
                            testTag = "export_image_row"
                        )
                    }
                }
            }

            // Colophon Footer
            item(key = "preferences_colophon") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Zenith · v1.1.0 · Dedicated to Quiet Contemplation",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = palette.subtleInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Ma (Space) · Preserving room for thoughts",
                        fontFamily = InterFontFamily,
                        fontSize = 10.sp,
                        color = palette.subtleInk,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Note Picker Dialog for PDF or Long Scroll Image export
    if (exportPickerMode != null) {
        val mode = exportPickerMode!!
        AlertDialog(
            onDismissRequest = { exportPickerMode = null },
            containerColor = palette.cardSurface,
            title = {
                Text(
                    text = if (mode == SingleNoteExportMode.PDF) "Select Note for PDF" else "Select Note for Scroll Image",
                    fontFamily = NewsreaderFontFamily,
                    fontSize = 20.sp,
                    color = palette.ink
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.notes.isEmpty()) {
                        Text(
                            text = "No active notes available to export.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = palette.mutedInk
                        )
                    } else {
                        uiState.notes.take(8).forEach { note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.surfaceElevated)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .clickable {
                                        exportPickerMode = null
                                        if (mode == SingleNoteExportMode.PDF) {
                                            onExportPdf(note)
                                        } else {
                                            onExportLongScrollImage(note)
                                        }
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title.ifBlank { "Untitled Thought" },
                                        fontFamily = NewsreaderFontFamily,
                                        fontSize = 15.sp,
                                        color = palette.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${note.notebook} · ${note.wordCount} words",
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.subtleInk
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = palette.subtleInk,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { exportPickerMode = null }) {
                    Text("Cancel", fontFamily = InterFontFamily, color = palette.ink)
                }
            }
        )
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    val palette = LocalZenithPalette.current
    Column {
        Text(
            text = title,
            fontFamily = NewsreaderFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = palette.ink
        )
        Text(
            text = subtitle,
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            color = palette.subtleInk
        )
    }
}

@Composable
private fun AtmosphereTile(
    tone: AtmosphereTone,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.cardSurface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) palette.ink else palette.border,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("atmosphere_tile_${tone.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(tone.background)
                    .border(1.dp, palette.border, CircleShape)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(palette.ink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = palette.background,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = tone.displayName,
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = palette.ink
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tone.subtitle,
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            color = palette.subtleInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val palette = LocalZenithPalette.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
            .padding(3.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selected) palette.cardSurface else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) palette.ink else palette.subtleInk
                )
            }
        }
    }
}

@Composable
private fun FocusToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = LocalZenithPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = palette.ink
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = palette.subtleInk
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.background,
                checkedTrackColor = palette.ink,
                uncheckedThumbColor = palette.cardSurface,
                uncheckedTrackColor = palette.surfaceElevated,
                uncheckedBorderColor = palette.border
            )
        )
    }
}

@Composable
private fun ExportActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = LocalZenithPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surfaceElevated)
                .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.ink,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = palette.ink
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                color = palette.subtleInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = palette.subtleInk,
            modifier = Modifier.size(18.dp)
        )
    }
}
