package com.example.kotlin_app.data

import kotlinx.coroutines.flow.Flow

class StudySessionRepository(
    private val studySessionDao: StudySessionDao
) {

    fun getSessionsForCourse(courseId: Long): Flow<List<StudySession>> =
        studySessionDao.getSessionsForCourse(courseId)

    fun getTotalMinutesForCourse(courseId: Long): Flow<Int?> =
        studySessionDao.getTotalMinutesForCourse(courseId)

    suspend fun upsertSession(session: StudySession): Long =
        studySessionDao.upsertSession(session)

    suspend fun deleteSession(session: StudySession) =
        studySessionDao.deleteSession(session)
}

