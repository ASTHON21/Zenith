package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenithPalette
import com.example.ui.theme.LocalZenithPreferences
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.viewmodel.AppTab

@Composable
fun ZenScreenHeader(
    rightSectionLabel: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Zenith",
                fontFamily = NewsreaderFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = palette.ink,
                modifier = Modifier.testTag("zenith_brand_title")
            )
            Text(
                text = rightSectionLabel.uppercase(),
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.4.sp,
                color = palette.subtleInk
            )
        }
        HorizontalDivider(thickness = 1.dp, color = palette.border)
    }
}

@Composable
fun ZenBottomNavigationBar(
    activeTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    val prefs = LocalZenithPreferences.current
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
    ) {
        HorizontalDivider(thickness = 1.dp, color = palette.border)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = tab == activeTab
                val icon = when (tab) {
                    AppTab.NOTES -> if (isSelected) Icons.AutoMirrored.Filled.Notes else Icons.AutoMirrored.Outlined.Notes
                    AppTab.FOLDERS -> if (isSelected) Icons.Filled.Folder else Icons.Outlined.Folder
                    AppTab.PREFERENCES -> if (isSelected) Icons.Filled.Tune else Icons.Outlined.Tune
                }
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (prefs.hapticFeedback) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onSelectTab(tab)
                        }
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("bottom_tab_${tab.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) palette.ink else palette.subtleInk,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) palette.ink else palette.subtleInk
                    )
                }
            }
        }
    }
}

@Composable
fun ZenCheckboxBox(
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalZenithPalette.current
    val prefs = LocalZenithPreferences.current
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .size(18.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(
                width = 1.2.dp,
                color = if (isChecked) palette.ink else palette.subtleInk,
                shape = RoundedCornerShape(3.dp)
            )
            .background(if (isChecked) palette.ink else Color.Transparent)
            .clickable {
                if (prefs.hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onToggle()
            },
        contentAlignment = Alignment.Center
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = palette.background,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
