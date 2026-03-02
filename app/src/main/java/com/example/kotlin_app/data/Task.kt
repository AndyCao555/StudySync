package com.example.kotlin_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
+)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val courseId: Long,
    val title: String,
    val description: String?,
    val dueDate: LocalDate?,
    val priority: Int,
    val isCompleted: Boolean,
)

