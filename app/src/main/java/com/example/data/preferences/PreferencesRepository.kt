package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zenith_preferences")

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val ATMOSPHERE = stringPreferencesKey("atmosphere_tone")
        val FONT_VOICE = stringPreferencesKey("font_voice")
        val LINE_SPACING = stringPreferencesKey("line_spacing")
        val BODY_FONT_SIZE = intPreferencesKey("body_font_size")
        val TYPEWRITER_CENTERING = booleanPreferencesKey("typewriter_centering")
        val SUBTLE_WORD_COUNT = booleanPreferencesKey("subtle_word_count")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    }

    val preferencesFlow: Flow<ZenithPreferences> = context.dataStore.data.map { prefs ->
        val atmosphere = prefs[Keys.ATMOSPHERE]?.let { name ->
            AtmosphereTone.entries.firstOrNull { it.name == name }
        } ?: AtmosphereTone.WARM_PAPER

        val fontVoice = prefs[Keys.FONT_VOICE]?.let { name ->
            EditorFontVoice.entries.firstOrNull { it.name == name }
        } ?: EditorFontVoice.SERIF

        val lineSpacing = prefs[Keys.LINE_SPACING]?.let { name ->
            LineSpacingMode.entries.firstOrNull { it.name == name }
        } ?: LineSpacingMode.RELAXED

        val fontSize = (prefs[Keys.BODY_FONT_SIZE] ?: 15).coerceIn(12, 22)

        ZenithPreferences(
            atmosphere = atmosphere,
            fontVoice = fontVoice,
            lineSpacing = lineSpacing,
            bodyFontSizeSp = fontSize,
            typewriterCentering = prefs[Keys.TYPEWRITER_CENTERING] ?: true,
            subtleWordCount = prefs[Keys.SUBTLE_WORD_COUNT] ?: true,
            hapticFeedback = prefs[Keys.HAPTIC_FEEDBACK] ?: false
        )
    }

    suspend fun setAtmosphere(tone: AtmosphereTone) {
        context.dataStore.edit { it[Keys.ATMOSPHERE] = tone.name }
    }

    suspend fun setFontVoice(voice: EditorFontVoice) {
        context.dataStore.edit { it[Keys.FONT_VOICE] = voice.name }
    }

    suspend fun setLineSpacing(mode: LineSpacingMode) {
        context.dataStore.edit { it[Keys.LINE_SPACING] = mode.name }
    }

    suspend fun setBodyFontSize(sizeSp: Int) {
        context.dataStore.edit { it[Keys.BODY_FONT_SIZE] = sizeSp.coerceIn(12, 22) }
    }

    suspend fun setTypewriterCentering(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TYPEWRITER_CENTERING] = enabled }
    }

    suspend fun setSubtleWordCount(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SUBTLE_WORD_COUNT] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }
}
