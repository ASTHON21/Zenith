package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val notebook: String,
    val tags: String,
    val status: String,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

object NoteTagConverter {
    fun fromList(tags: List<String>): String = tags.joinToString(",")
    fun toList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

fun NoteEntity.toNote(): Note = Note(
    id = id,
    title = title,
    content = content,
    notebook = notebook,
    tags = NoteTagConverter.toList(tags),
    status = status,
    isPinned = isPinned,
    isArchived = isArchived,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    notebook = notebook,
    tags = NoteTagConverter.fromList(tags),
    status = status,
    isPinned = isPinned,
    isArchived = isArchived,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)
