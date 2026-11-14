package com.example.data.model

data class Notebook(
    val id: Long = 0,
    val orderIndex: Int = 1,
    val name: String,
    val subtitle: String,
    val createdAt: Long = System.currentTimeMillis()
)
