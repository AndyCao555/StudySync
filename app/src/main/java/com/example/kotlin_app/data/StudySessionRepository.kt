package com.example.kotlin_app.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class StudySessionRepository(
    private val studySessionDao: StudySessionDao,
    private val supabaseSync: SupabaseSyncService? = null
) {

    fun getAllSessions(): Flow<List<StudySession>> =
        studySessionDao.getAllSessions()

    fun getSessionsForCourse(courseId: Long): Flow<List<StudySession>> =
        studySessionDao.getSessionsForCourse(courseId)

    fun getTotalMinutes(): Flow<Int?> =
        studySessionDao.getTotalMinutes()

    fun getTotalMinutesForCourse(courseId: Long): Flow<Int?> =
        studySessionDao.getTotalMinutesForCourse(courseId)

    suspend fun upsertSession(session: StudySession): Long {
        val id = studySessionDao.upsertSession(session)
        val updatedSession = session.copy(id = id)

        supabaseSync?.let {
            try {
                it.syncStudySessionToSupabase(updatedSession)
            } catch (e: Exception) {
                Log.e("StudySessionRepository", "Supabase sync failed", e)
            }
        }

        return id
    }

    suspend fun deleteSession(session: StudySession) {
        studySessionDao.deleteSession(session)

        supabaseSync?.let {
            try {
                it.deleteStudySessionFromSupabase(session.id)
            } catch (e: Exception) {
                Log.e("StudySessionRepository", "Supabase delete failed", e)
            }
        }
    }
}
