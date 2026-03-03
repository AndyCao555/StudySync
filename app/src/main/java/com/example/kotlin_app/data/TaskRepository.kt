package com.example.kotlin_app.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val supabaseSync: SupabaseSyncService? = null
) {

    fun getTasksForCourse(courseId: Long): Flow<List<Task>> =
        taskDao.getTasksForCourse(courseId)

    fun getTaskById(taskId: Long): Flow<Task?> =
        taskDao.getTaskById(taskId)

    suspend fun upsertTask(task: Task): Long {
        val id = taskDao.upsertTask(task)
        val updatedTask = task.copy(id = id)
        
        // Sync to Supabase
        supabaseSync?.let {
            try {
                Log.d("TaskRepository", "Attempting to sync task to Supabase: ${updatedTask.title}")
                it.syncTaskToSupabase(updatedTask)
                Log.d("TaskRepository", "Task synced successfully to Supabase")
            } catch (e: Exception) {
                Log.e("TaskRepository", "Supabase sync failed", e)
                Log.e("TaskRepository", "Error details: ${e.message}", e)
            }
        } ?: Log.w("TaskRepository", "SupabaseSyncService is null - sync skipped")
        
        return id
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        
        // Delete from Supabase
        supabaseSync?.let {
            try {
                Log.d("TaskRepository", "Attempting to delete task from Supabase: id=${task.id}, title=${task.title}")
                it.deleteTaskFromSupabase(task.id)
                Log.d("TaskRepository", "Task deleted successfully from Supabase")
            } catch (e: Exception) {
                Log.e("TaskRepository", "Supabase delete failed", e)
                Log.e("TaskRepository", "Error details: ${e.message}", e)
            }
        } ?: Log.w("TaskRepository", "SupabaseSyncService is null - delete sync skipped")
    }
}

