package com.example.kotlin_app.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

/**
 * Background worker that periodically syncs local Room data to Supabase.
 *
 * Runs via WorkManager even when the app is not in the foreground,
 * ensuring cloud data stays up-to-date. Uses CoroutineWorker for
 * structured concurrency with Kotlin coroutines.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncWorker"
        const val WORK_NAME = "studysync_periodic_sync"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting periodic Supabase sync...")

        return try {
            val database = StudySyncDatabase.getInstance(applicationContext)
            val supabaseSync = SupabaseSyncService()

            // Check if Supabase client is available
            if (SupabaseClient.client == null) {
                Log.w(TAG, "Supabase client not configured — skipping sync")
                return Result.success()
            }

            // 1. Sync all courses
            val courses = database.courseDao().getAllCourses().first()
            Log.d(TAG, "Syncing ${courses.size} courses...")
            for (course in courses) {
                try {
                    supabaseSync.syncCourseToSupabase(course)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync course: ${course.name}", e)
                }
            }

            // 2. Sync all tasks
            val tasks = database.taskDao().getAllTasks().first()
            Log.d(TAG, "Syncing ${tasks.size} tasks...")
            for (task in tasks) {
                try {
                    supabaseSync.syncTaskToSupabase(task)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync task: ${task.title}", e)
                }
            }

            // 3. Sync all study sessions
            val sessions = database.studySessionDao().getAllSessions().first()
            Log.d(TAG, "Syncing ${sessions.size} study sessions...")
            for (session in sessions) {
                try {
                    supabaseSync.syncStudySessionToSupabase(session)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync study session: ${session.id}", e)
                }
            }

            Log.d(TAG, "Periodic sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Periodic sync failed", e)
            // Retry on failure — WorkManager will respect backoff policy
            Result.retry()
        }
    }
}
