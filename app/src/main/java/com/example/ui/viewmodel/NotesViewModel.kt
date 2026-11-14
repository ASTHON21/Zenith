package com.example.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.ExportManager
import com.example.data.local.AppDatabase
import com.example.data.model.Note
import com.example.data.model.Notebook
import com.example.data.model.TemporalBucket
import com.example.data.preferences.AtmosphereTone
import com.example.data.preferences.EditorFontVoice
import com.example.data.preferences.LineSpacingMode
import com.example.data.preferences.PreferencesRepository
import com.example.data.preferences.ZenithPreferences
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
    NOTES("Notes"),
    FOLDERS("Folders"),
    PREFERENCES("Preferences")
}

enum class NoteTypeFilter(val label: String) {
    ALL("All Formats"),
    CHECKLISTS("Checklists"),
    EXCERPTS("Reading Excerpts"),
    PINNED("Pinned Only")
}

data class NotebookSummary(
    val notebook: Notebook,
    val noteCount: Int
)

data class TagSummary(
    val tag: String,
    val count: Int
)

data class NotesUiState(
    val activeTab: AppTab = AppTab.NOTES,
    val notes: List<Note> = emptyList(),
    val archivedNotes: List<Note> = emptyList(),
    val notebooks: List<Notebook> = emptyList(),
    val notebookSummaries: List<NotebookSummary> = emptyList(),
    val tagSummaries: List<TagSummary> = emptyList(),
    val pinnedReflection: Note? = null,
    val todayNotes: List<Note> = emptyList(),
    val thisWeekNotes: List<Note> = emptyList(),
    val earlierNotes: List<Note> = emptyList(),
    val filteredCount: Int = 0,
    val totalActiveNotesCount: Int = 0,
    val activeNote: Note? = null,
    val searchQuery: String = "",
    val selectedNotebook: String = "All",
    val selectedTag: String? = null,
    val typeFilter: NoteTypeFilter = NoteTypeFilter.ALL,
    val isEditorOpen: Boolean = false,
    val isPreviewMode: Boolean = false,
    val zenFocusMode: Boolean = false,
    val preferences: ZenithPreferences = ZenithPreferences(),
    val customTags: List<String> = emptyList(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isExporting: Boolean = false
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val preferencesRepository: PreferencesRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NoteRepository(db.noteDao(), db.notebookDao())
        preferencesRepository = PreferencesRepository(application)
    }

    private val _activeTab = MutableStateFlow(AppTab.NOTES)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedNotebook = MutableStateFlow("All")
    private val _selectedTag = MutableStateFlow<String?>(null)
    private val _typeFilter = MutableStateFlow(NoteTypeFilter.ALL)
    private val _activeNote = MutableStateFlow<Note?>(null)
    private val _isEditorOpen = MutableStateFlow(false)
    private val _isPreviewMode = MutableStateFlow(false)
    private val _zenFocusMode = MutableStateFlow(false)
    private val _customTags = MutableStateFlow<List<String>>(emptyList())
    private val _isExporting = MutableStateFlow(false)

    private val undoStack = mutableListOf<Pair<String, String>>()
    private val redoStack = mutableListOf<Pair<String, String>>()
    private val _canUndo = MutableStateFlow(false)
    private val _canRedo = MutableStateFlow(false)

    private val filterFlow = combine(
        _activeTab,
        _searchQuery,
        _selectedNotebook,
        _selectedTag,
        _typeFilter
    ) { tab, query, notebook, tag, typeFilter ->
        FilterParams(tab, query, notebook, tag, typeFilter)
    }

    private val editorMetaFlow = combine(
        _activeNote,
        _isEditorOpen,
        _isPreviewMode,
        _zenFocusMode,
        _canUndo,
        _canRedo
    ) { values ->
        EditorParams(
            activeNote = values[0] as Note?,
            isEditorOpen = values[1] as Boolean,
            isPreviewMode = values[2] as Boolean,
            zenFocusMode = values[3] as Boolean,
            canUndo = values[4] as Boolean,
            canRedo = values[5] as Boolean
        )
    }

    val uiState: StateFlow<NotesUiState> = combine(
        repository.allNotes,
        repository.allNotebooks,
        preferencesRepository.preferencesFlow,
        filterFlow,
        editorMetaFlow,
        _customTags,
        _isExporting
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val notes = values[0] as List<Note>
        @Suppress("UNCHECKED_CAST")
        val notebooks = values[1] as List<Notebook>
        val prefs = values[2] as ZenithPreferences
        val filters = values[3] as FilterParams
        val editor = values[4] as EditorParams
        @Suppress("UNCHECKED_CAST")
        val customTags = values[5] as List<String>
        val isExporting = values[6] as Boolean

        val activeNotes = notes.filter { !it.isArchived }
        val archivedNotes = notes.filter { it.isArchived }

        val notebookSummaries = notebooks.map { nb ->
            NotebookSummary(
                notebook = nb,
                noteCount = activeNotes.count { it.notebook.equals(nb.name, ignoreCase = true) }
            )
        }

        val tagCountMap = linkedMapOf<String, Int>()
        activeNotes.forEach { note ->
            note.tags.forEach { t ->
                val clean = t.trim()
                if (clean.isNotEmpty()) {
                    tagCountMap[clean] = (tagCountMap[clean] ?: 0) + 1
                }
            }
        }
        customTags.forEach { ct ->
            if (!tagCountMap.containsKey(ct)) tagCountMap[ct] = 0
        }
        val tagSummaries = tagCountMap.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { TagSummary(it.key, it.value) }

        val filtered = activeNotes.filter { note ->
            val matchesNotebook = filters.notebook == "All" ||
                note.notebook.equals(filters.notebook, ignoreCase = true)

            val matchesTag = filters.tag == null ||
                note.tags.any { it.equals(filters.tag, ignoreCase = true) }

            val matchesType = when (filters.typeFilter) {
                NoteTypeFilter.ALL -> true
                NoteTypeFilter.CHECKLISTS -> note.checklistItems.isNotEmpty()
                NoteTypeFilter.EXCERPTS -> note.isQuoteFocused
                NoteTypeFilter.PINNED -> note.isPinned
            }

            val q = filters.query.trim()
            val matchesQuery = q.isEmpty() ||
                note.title.contains(q, ignoreCase = true) ||
                note.content.contains(q, ignoreCase = true) ||
                note.notebook.contains(q, ignoreCase = true) ||
                note.tags.any { it.contains(q, ignoreCase = true) }

            matchesNotebook && matchesTag && matchesType && matchesQuery
        }

        val showPinnedHero = filters.query.isBlank() &&
            filters.tag == null &&
            filters.typeFilter == NoteTypeFilter.ALL &&
            filters.notebook == "All"

        val pinnedHero = if (showPinnedHero) filtered.firstOrNull { it.isPinned } else null
        val feedNotes = if (pinnedHero != null) filtered.filterNot { it.id == pinnedHero.id } else filtered

        val today = feedNotes.filter { it.temporalBucket == TemporalBucket.TODAY }
        val thisWeek = feedNotes.filter { it.temporalBucket == TemporalBucket.THIS_WEEK }
        val earlier = feedNotes.filter { it.temporalBucket == TemporalBucket.EARLIER }

        // Keep activeNote synced with DB updates (e.g., if checklist item changed)
        val syncedActive = editor.activeNote?.let { current ->
            notes.firstOrNull { it.id == current.id } ?: current
        }

        NotesUiState(
            activeTab = filters.tab,
            notes = activeNotes,
            archivedNotes = archivedNotes,
            notebooks = notebooks,
            notebookSummaries = notebookSummaries,
            tagSummaries = tagSummaries,
            pinnedReflection = pinnedHero,
            todayNotes = today,
            thisWeekNotes = thisWeek,
            earlierNotes = earlier,
            filteredCount = filtered.size,
            totalActiveNotesCount = activeNotes.size,
            activeNote = syncedActive,
            searchQuery = filters.query,
            selectedNotebook = filters.notebook,
            selectedTag = filters.tag,
            typeFilter = filters.typeFilter,
            isEditorOpen = editor.isEditorOpen,
            isPreviewMode = editor.isPreviewMode,
            zenFocusMode = editor.zenFocusMode,
            preferences = prefs,
            customTags = customTags,
            canUndo = editor.canUndo,
            canRedo = editor.canRedo,
            isExporting = isExporting
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState()
    )

    fun selectTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectNotebookFilter(notebookName: String) {
        _selectedNotebook.value = notebookName
    }

    fun openNotebookFromTaxonomy(notebookName: String) {
        _selectedNotebook.value = notebookName
        _selectedTag.value = null
        _activeTab.value = AppTab.NOTES
    }

    fun selectTagFilter(tag: String?) {
        _selectedTag.value = if (_selectedTag.value == tag) null else tag
    }

    fun openTagFromTaxonomy(tag: String) {
        _selectedTag.value = tag
        _selectedNotebook.value = "All"
        _activeTab.value = AppTab.NOTES
    }

    fun setTypeFilter(filter: NoteTypeFilter) {
        _typeFilter.value = filter
    }

    fun resetHomeFilters() {
        _searchQuery.value = ""
        _selectedNotebook.value = "All"
        _selectedTag.value = null
        _typeFilter.value = NoteTypeFilter.ALL
    }

    fun openNote(note: Note) {
        _activeNote.value = note
        _isEditorOpen.value = true
        _isPreviewMode.value = false
        _zenFocusMode.value = false
        clearHistory()
    }

    fun createNewNote(
        notebook: String = if (_selectedNotebook.value == "All") {
            uiState.value.notebooks.firstOrNull()?.name ?: "Unfiled"
        } else {
            _selectedNotebook.value
        }
    ) {
        val initialTags = _selectedTag.value?.let { listOf(it) } ?: emptyList()
        val newNote = Note(
            id = 0,
            title = "",
            content = "",
            notebook = notebook,
            tags = initialTags,
            status = "Draft",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            val id = repository.saveNote(newNote)
            val created = newNote.copy(id = id)
            _activeNote.value = created
            _isEditorOpen.value = true
            _isPreviewMode.value = false
            _zenFocusMode.value = false
            clearHistory()
        }
    }

    fun updateActiveNoteTitle(newTitle: String) {
        val current = _activeNote.value ?: return
        recordHistory(current.title, current.content)
        val updated = current.copy(title = newTitle, updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun updateActiveNoteProse(newProse: String) {
        val current = _activeNote.value ?: return
        recordHistory(current.title, current.content)
        val checklistLines = current.content.lines().filter { line ->
            val t = line.trimStart()
            t.startsWith("- [ ] ") || t.startsWith("- [x] ") || t.startsWith("- [X] ")
        }
        val combined = if (checklistLines.isEmpty()) {
            newProse
        } else {
            val trimmedProse = newProse.trimEnd()
            if (trimmedProse.isEmpty()) {
                checklistLines.joinToString("\n")
            } else {
                "$trimmedProse\n\n${checklistLines.joinToString("\n")}"
            }
        }
        val updated = current.copy(content = combined, updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun updateActiveNoteNotebook(notebook: String) {
        val current = _activeNote.value ?: return
        val updated = current.copy(notebook = notebook, updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun cycleActiveNoteStatus() {
        val current = _activeNote.value ?: return
        val statuses = listOf("Draft", "Draft · In Review", "Final", "Journal Entry")
        val nextIndex = (statuses.indexOf(current.status) + 1) % statuses.size
        val updated = current.copy(status = statuses[nextIndex], updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun addActiveNoteTag(tag: String) {
        val cleanTag = tag.trim().removePrefix("#")
        if (cleanTag.isEmpty()) return
        val current = _activeNote.value ?: return
        if (current.tags.any { it.equals(cleanTag, ignoreCase = true) }) return
        val updated = current.copy(tags = current.tags + cleanTag, updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun removeActiveNoteTag(tag: String) {
        val current = _activeNote.value ?: return
        val updated = current.copy(
            tags = current.tags.filterNot { it.equals(tag, ignoreCase = true) },
            updatedAt = System.currentTimeMillis()
        )
        _activeNote.value = updated
        persistNote(updated)
    }

    fun toggleNoteChecklistItem(note: Note, lineIndex: Int) {
        val lines = note.content.lines().toMutableList()
        if (lineIndex in lines.indices) {
            val line = lines[lineIndex]
            val trimmed = line.trimStart()
            val updatedLine = when {
                trimmed.startsWith("- [ ] ") -> line.replaceFirst("- [ ] ", "- [x] ")
                trimmed.startsWith("- [x] ") -> line.replaceFirst("- [x] ", "- [ ] ")
                trimmed.startsWith("- [X] ") -> line.replaceFirst("- [X] ", "- [ ] ")
                else -> line
            }
            lines[lineIndex] = updatedLine
            val updated = note.copy(
                content = lines.joinToString("\n"),
                updatedAt = System.currentTimeMillis()
            )
            if (_activeNote.value?.id == note.id) {
                recordHistory(note.title, note.content)
                _activeNote.value = updated
            }
            persistNote(updated)
        }
    }

    fun addChecklistItemToActiveNote(taskText: String) {
        val clean = taskText.trim()
        if (clean.isEmpty()) return
        val current = _activeNote.value ?: return
        recordHistory(current.title, current.content)
        val newContent = if (current.content.isBlank()) {
            "- [ ] $clean"
        } else {
            "${current.content.trimEnd()}\n- [ ] $clean"
        }
        val updated = current.copy(content = newContent, updatedAt = System.currentTimeMillis())
        _activeNote.value = updated
        persistNote(updated)
    }

    fun removeChecklistItemFromActiveNote(lineIndex: Int) {
        val current = _activeNote.value ?: return
        val lines = current.content.lines().toMutableList()
        if (lineIndex in lines.indices) {
            recordHistory(current.title, current.content)
            lines.removeAt(lineIndex)
            val updated = current.copy(
                content = lines.joinToString("\n").trimEnd(),
                updatedAt = System.currentTimeMillis()
            )
            _activeNote.value = updated
            persistNote(updated)
        }
    }

    fun insertMarkdownIntoProse(prefix: String, suffix: String = "") {
        val current = _activeNote.value ?: return
        val prose = current.proseContent
        val newProse = if (prose.isEmpty() || prose.endsWith("\n")) {
            "$prose$prefix$suffix"
        } else {
            "$prose\n$prefix$suffix"
        }
        updateActiveNoteProse(newProse)
    }

    fun togglePin(note: Note) {
        val updated = note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis())
        if (_activeNote.value?.id == note.id) {
            _activeNote.value = updated
        }
        persistNote(updated)
    }

    fun toggleArchive(note: Note) {
        val updated = note.copy(isArchived = !note.isArchived, updatedAt = System.currentTimeMillis())
        if (_activeNote.value?.id == note.id) {
            _activeNote.value = null
            _isEditorOpen.value = false
        }
        persistNote(updated)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note.id)
            if (_activeNote.value?.id == note.id) {
                _activeNote.value = null
                _isEditorOpen.value = false
                _zenFocusMode.value = false
            }
        }
    }

    fun closeEditor() {
        val current = _activeNote.value
        if (current != null && current.title.isBlank() && current.content.isBlank()) {
            viewModelScope.launch { repository.deleteNote(current.id) }
        }
        _isEditorOpen.value = false
        _activeNote.value = null
        _isPreviewMode.value = false
        _zenFocusMode.value = false
    }

    fun togglePreviewMode() {
        _isPreviewMode.value = !_isPreviewMode.value
    }

    fun toggleZenFocusMode() {
        _zenFocusMode.value = !_zenFocusMode.value
    }

    fun createNotebook(name: String, subtitle: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createNotebook(name, subtitle)
        }
    }

    fun deleteNotebook(notebook: Notebook) {
        val currentNotebooks = uiState.value.notebooks
        val fallback = currentNotebooks.firstOrNull { it.id != notebook.id }?.name ?: "Unfiled"
        viewModelScope.launch {
            repository.deleteNotebook(notebook, fallback)
            if (_selectedNotebook.value.equals(notebook.name, ignoreCase = true)) {
                _selectedNotebook.value = "All"
            }
        }
    }

    fun addCustomTag(tag: String) {
        val clean = tag.trim().removePrefix("#")
        if (clean.isEmpty()) return
        if (_customTags.value.none { it.equals(clean, ignoreCase = true) }) {
            _customTags.value = _customTags.value + clean
        }
    }

    // Preferences updates
    fun setAtmosphere(tone: AtmosphereTone) = viewModelScope.launch {
        preferencesRepository.setAtmosphere(tone)
    }

    fun setFontVoice(voice: EditorFontVoice) = viewModelScope.launch {
        preferencesRepository.setFontVoice(voice)
    }

    fun setLineSpacing(mode: LineSpacingMode) = viewModelScope.launch {
        preferencesRepository.setLineSpacing(mode)
    }

    fun adjustBodyFontSize(delta: Int) = viewModelScope.launch {
        val next = (uiState.value.preferences.bodyFontSizeSp + delta).coerceIn(12, 22)
        preferencesRepository.setBodyFontSize(next)
    }

    fun setTypewriterCentering(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setTypewriterCentering(enabled)
    }

    fun setSubtleWordCount(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setSubtleWordCount(enabled)
    }

    fun setHapticFeedback(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setHapticFeedback(enabled)
    }

    // Real Exports via Android Share Sheet
    fun exportNoteAsPdf(note: Note) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val context = getApplication<Application>()
                val uri = ExportManager.exportTypesetPdf(context, note, uiState.value.preferences)
                ExportManager.launchShareSheet(context, uri, "application/pdf", "Export Typeset PDF")
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "PDF export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportAllAsMarkdownZip() {
        val notesToExport = uiState.value.notes
        if (notesToExport.isEmpty()) {
            Toast.makeText(getApplication(), "No notes available to export.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val context = getApplication<Application>()
                val uri = ExportManager.exportMarkdownZip(context, notesToExport)
                ExportManager.launchShareSheet(context, uri, "application/zip", "Export Markdown Package (.zip)")
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Archive export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportNoteAsLongScrollImage(note: Note) {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val context = getApplication<Application>()
                val uri = ExportManager.exportLongScrollImage(context, note, uiState.value.preferences)
                ExportManager.launchShareSheet(context, uri, "image/png", "Share Long Scroll Image")
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Image export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isExporting.value = false
            }
        }
    }

    // Undo / Redo
    private fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false
    }

    private fun recordHistory(title: String, content: String) {
        if (undoStack.lastOrNull() != Pair(title, content)) {
            undoStack.add(Pair(title, content))
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
            _canUndo.value = true
            _canRedo.value = false
        }
    }

    fun undo() {
        val current = _activeNote.value ?: return
        if (undoStack.isNotEmpty()) {
            val lastState = undoStack.removeAt(undoStack.size - 1)
            redoStack.add(Pair(current.title, current.content))
            val updated = current.copy(
                title = lastState.first,
                content = lastState.second,
                updatedAt = System.currentTimeMillis()
            )
            _activeNote.value = updated
            persistNote(updated)
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
        }
    }

    fun redo() {
        val current = _activeNote.value ?: return
        if (redoStack.isNotEmpty()) {
            val nextState = redoStack.removeAt(redoStack.size - 1)
            undoStack.add(Pair(current.title, current.content))
            val updated = current.copy(
                title = nextState.first,
                content = nextState.second,
                updatedAt = System.currentTimeMillis()
            )
            _activeNote.value = updated
            persistNote(updated)
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
        }
    }

    private fun persistNote(note: Note) {
        viewModelScope.launch {
            repository.saveNote(note)
        }
    }

    private data class FilterParams(
        val tab: AppTab,
        val query: String,
        val notebook: String,
        val tag: String?,
        val typeFilter: NoteTypeFilter
    )

    private data class EditorParams(
        val activeNote: Note?,
        val isEditorOpen: Boolean,
        val isPreviewMode: Boolean,
        val zenFocusMode: Boolean,
        val canUndo: Boolean,
        val canRedo: Boolean
    )
}
