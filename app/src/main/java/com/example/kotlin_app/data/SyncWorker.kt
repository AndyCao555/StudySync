package com.example.kotlin_app.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncWorker"
        const val WORK_NAME = "studysync_periodic_sync"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting periodic Supabase sync")

        return try {
            val database = StudySyncDatabase.getInstance(applicationContext)
            val supabaseSync = SupabaseSyncService()

            if (SupabaseClient.client == null) {
                Log.w(TAG, "Supabase client not configured, skipping sync")
                return Result.success()
            }

            val courses = database.courseDao().getAllCourses().first()
            for (course in courses) {
                try {
                    supabaseSync.syncCourseToSupabase(course)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync course: ${course.name}", e)
                }
            }

            val tasks = database.taskDao().getAllTasks().first()
            for (task in tasks) {
                try {
                    supabaseSync.syncTaskToSupabase(task)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync task: ${task.title}", e)
                }
            }

            val sessions = database.studySessionDao().getAllSessions().first()
            for (session in sessions) {
                try {
                    supabaseSync.syncStudySessionToSupabase(session)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync study session: ${session.id}", e)
                }
            }

            Log.d(TAG, "Periodic sync completed")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Periodic sync failed", e)
            Result.retry()
        }
    }
}
