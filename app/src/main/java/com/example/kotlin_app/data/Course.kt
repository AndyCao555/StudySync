package com.example.kotlin_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val colorHex: String,
)

