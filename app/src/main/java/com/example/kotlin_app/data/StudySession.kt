package com.example.kotlin_app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val courseId: Long,
    val date: LocalDate,
    val durationMinutes: Int,
)

