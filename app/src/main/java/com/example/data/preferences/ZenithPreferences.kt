package com.example.data.preferences

import androidx.compose.ui.graphics.Color

enum class AtmosphereTone(
    val displayName: String,
    val subtitle: String,
    val background: Color,
    val cardSurface: Color,
    val surfaceElevated: Color,
    val ink: Color,
    val mutedInk: Color,
    val subtleInk: Color,
    val border: Color,
    val isDark: Boolean
) {
    WARM_PAPER(
        displayName = "Warm Paper",
        subtitle = "Soft & tactile cream",
        background = Color(0xFFFAF9F6),
        cardSurface = Color(0xFFFFFFFF),
        surfaceElevated = Color(0xFFF3F1EC),
        ink = Color(0xFF1A1A1A),
        mutedInk = Color(0xFF555555),
        subtleInk = Color(0xFF8A8884),
        border = Color(0xFFEAE8E3),
        isDark = false
    ),
    PURE_WHITE(
        displayName = "Pure White",
        subtitle = "Unblemished winter snow",
        background = Color(0xFFFFFFFF),
        cardSurface = Color(0xFFFFFFFF),
        surfaceElevated = Color(0xFFF5F5F5),
        ink = Color(0xFF141414),
        mutedInk = Color(0xFF525252),
        subtleInk = Color(0xFF888888),
        border = Color(0xFFE8E8E8),
        isDark = false
    ),
    SOFT_MIST(
        displayName = "Soft Mist",
        subtitle = "Subtle neutral gray",
        background = Color(0xFFEFEFEF),
        cardSurface = Color(0xFFF8F8F8),
        surfaceElevated = Color(0xFFE5E5E3),
        ink = Color(0xFF1D1D1C),
        mutedInk = Color(0xFF585855),
        subtleInk = Color(0xFF858580),
        border = Color(0xFFDCDCD8),
        isDark = false
    ),
    MIDNIGHT_INK(
        displayName = "Midnight Ink",
        subtitle = "Deep immersive black",
        background = Color(0xFF141413),
        cardSurface = Color(0xFF1D1D1B),
        surfaceElevated = Color(0xFF262623),
        ink = Color(0xFFF2F0EC),
        mutedInk = Color(0xFFA8A59E),
        subtleInk = Color(0xFF78756F),
        border = Color(0xFF2E2E2B),
        isDark = true
    )
}

enum class EditorFontVoice(val label: String) {
    SERIF("Serif"),
    MODERN_SANS("Modern Sans")
}

enum class LineSpacingMode(val label: String, val multiplier: Float) {
    RELAXED("Relaxed 1.8x", 1.8f),
    STANDARD("Standard 1.5x", 1.5f)
}

data class ZenithPreferences(
    val atmosphere: AtmosphereTone = AtmosphereTone.WARM_PAPER,
    val fontVoice: EditorFontVoice = EditorFontVoice.SERIF,
    val lineSpacing: LineSpacingMode = LineSpacingMode.RELAXED,
    val bodyFontSizeSp: Int = 15,
    val typewriterCentering: Boolean = true,
    val subtleWordCount: Boolean = true,
    val hapticFeedback: Boolean = false
)
