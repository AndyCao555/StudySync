package com.example.kotlin_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Query("SELECT * FROM study_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE courseId = :courseId ORDER BY date DESC")
    fun getSessionsForCourse(courseId: Long): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalMinutes(): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE courseId = :courseId")
    fun getTotalMinutesForCourse(courseId: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: StudySession): Long

    @Delete
    suspend fun deleteSession(session: StudySession)
}

