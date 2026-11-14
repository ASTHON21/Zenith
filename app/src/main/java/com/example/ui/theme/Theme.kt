package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.preferences.AtmosphereTone
import com.example.data.preferences.ZenithPreferences

@Immutable
data class ZenithPalette(
    val background: Color,
    val cardSurface: Color,
    val surfaceElevated: Color,
    val ink: Color,
    val mutedInk: Color,
    val subtleInk: Color,
    val border: Color,
    val isDark: Boolean
)

val LocalZenithPalette = staticCompositionLocalOf {
    ZenithPalette(
        background = AtmosphereTone.WARM_PAPER.background,
        cardSurface = AtmosphereTone.WARM_PAPER.cardSurface,
        surfaceElevated = AtmosphereTone.WARM_PAPER.surfaceElevated,
        ink = AtmosphereTone.WARM_PAPER.ink,
        mutedInk = AtmosphereTone.WARM_PAPER.mutedInk,
        subtleInk = AtmosphereTone.WARM_PAPER.subtleInk,
        border = AtmosphereTone.WARM_PAPER.border,
        isDark = false
    )
}

val LocalZenithPreferences = staticCompositionLocalOf { ZenithPreferences() }

val ZenithShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

@Composable
fun ZenithTheme(
    preferences: ZenithPreferences = ZenithPreferences(),
    content: @Composable () -> Unit
) {
    val tone = preferences.atmosphere
    val palette = ZenithPalette(
        background = tone.background,
        cardSurface = tone.cardSurface,
        surfaceElevated = tone.surfaceElevated,
        ink = tone.ink,
        mutedInk = tone.mutedInk,
        subtleInk = tone.subtleInk,
        border = tone.border,
        isDark = tone.isDark
    )

    val colorScheme = if (tone.isDark) {
        darkColorScheme(
            primary = tone.ink,
            onPrimary = tone.background,
            primaryContainer = tone.surfaceElevated,
            onPrimaryContainer = tone.ink,
            secondary = tone.mutedInk,
            onSecondary = tone.background,
            secondaryContainer = tone.surfaceElevated,
            onSecondaryContainer = tone.ink,
            background = tone.background,
            onBackground = tone.ink,
            surface = tone.cardSurface,
            onSurface = tone.ink,
            surfaceVariant = tone.surfaceElevated,
            onSurfaceVariant = tone.mutedInk,
            outline = tone.subtleInk,
            outlineVariant = tone.border,
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005)
        )
    } else {
        lightColorScheme(
            primary = tone.ink,
            onPrimary = tone.background,
            primaryContainer = tone.surfaceElevated,
            onPrimaryContainer = tone.ink,
            secondary = tone.mutedInk,
            onSecondary = tone.background,
            secondaryContainer = tone.surfaceElevated,
            onSecondaryContainer = tone.ink,
            background = tone.background,
            onBackground = tone.ink,
            surface = tone.cardSurface,
            onSurface = tone.ink,
            surfaceVariant = tone.surfaceElevated,
            onSurfaceVariant = tone.mutedInk,
            outline = tone.subtleInk,
            outlineVariant = tone.border,
            error = Color(0xFFBA1A1A),
            onError = Color.White
        )
    }

    CompositionLocalProvider(
        LocalZenithPalette provides palette,
        LocalZenithPreferences provides preferences
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ZenithTypography,
            shapes = ZenithShapes,
            content = content
        )
    }
}
