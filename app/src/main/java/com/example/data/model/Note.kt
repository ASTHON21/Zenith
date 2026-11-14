package com.example.data.model

import java.util.Calendar

data class ChecklistItem(
    val lineIndex: Int,
    val text: String,
    val isChecked: Boolean
)

enum class TemporalBucket(val label: String) {
    TODAY("TODAY"),
    THIS_WEEK("THIS WEEK"),
    EARLIER("EARLIER")
}

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val notebook: String = "Unfiled",
    val tags: List<String> = emptyList(),
    val status: String = "Draft",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val checklistItems: List<ChecklistItem>
        get() = content.lines().mapIndexedNotNull { index, line ->
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("- [ ] ") -> ChecklistItem(
                    lineIndex = index,
                    text = trimmed.removePrefix("- [ ] "),
                    isChecked = false
                )
                trimmed.startsWith("- [x] ") || trimmed.startsWith("- [X] ") -> ChecklistItem(
                    lineIndex = index,
                    text = trimmed.substring(6),
                    isChecked = true
                )
                else -> null
            }
        }

    val proseContent: String
        get() = content.lines()
            .filterNot { line ->
                val t = line.trimStart()
                t.startsWith("- [ ] ") || t.startsWith("- [x] ") || t.startsWith("- [X] ")
            }
            .joinToString("\n")
            .trimEnd()

    val isChecklistFocused: Boolean
        get() {
            if (checklistItems.isEmpty()) return false
            val nonChecklistNonEmptyLines = content.lines().count { line ->
                val t = line.trim()
                t.isNotEmpty() && !t.startsWith("- [ ] ") && !t.startsWith("- [x] ") && !t.startsWith("- [X] ")
            }
            return nonChecklistNonEmptyLines <= 1
        }

    val isQuoteFocused: Boolean
        get() = notebook.equals("Reading Excerpts", ignoreCase = true) ||
            proseContent.trimStart().startsWith("> ")

    val wordCount: Int
        get() = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size

    val charCount: Int
        get() = content.length

    val readingTimeMinutes: Int
        get() = maxOf(1, (wordCount + 199) / 200)

    val snippet: String
        get() = proseContent.lineSequence()
            .map {
                it.trim()
                    .removePrefix("### ")
                    .removePrefix("## ")
                    .removePrefix("# ")
                    .removePrefix("> ")
                    .removePrefix("- ")
                    .trim()
            }
            .firstOrNull { it.isNotEmpty() } ?: "Empty thought..."

    val temporalBucket: TemporalBucket
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfToday = cal.timeInMillis
            val startOfWeek = startOfToday - 6 * 86_400_000L
            return when {
                updatedAt >= startOfToday -> TemporalBucket.TODAY
                updatedAt >= startOfWeek -> TemporalBucket.THIS_WEEK
                else -> TemporalBucket.EARLIER
            }
        }
}
