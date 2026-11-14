package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Notebook

@Entity(tableName = "notebooks")
data class NotebookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderIndex: Int,
    val name: String,
    val subtitle: String,
    val createdAt: Long
)

fun NotebookEntity.toNotebook(): Notebook = Notebook(
    id = id,
    orderIndex = orderIndex,
    name = name,
    subtitle = subtitle,
    createdAt = createdAt
)

fun Notebook.toEntity(): NotebookEntity = NotebookEntity(
    id = id,
    orderIndex = orderIndex,
    name = name,
    subtitle = subtitle,
    createdAt = createdAt
)
