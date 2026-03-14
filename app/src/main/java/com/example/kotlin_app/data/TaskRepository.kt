package com.example.kotlin_app.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val supabaseSync: SupabaseSyncService? = null
) {

    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks()

    fun getUpcomingTasks(): Flow<List<Task>> =
        taskDao.getUpcomingTasks()

    fun getTasksForCourse(courseId: Long): Flow<List<Task>> =
        taskDao.getTasksForCourse(courseId)

    fun getTaskById(taskId: Long): Flow<Task?> =
        taskDao.getTaskById(taskId)

    suspend fun upsertTask(task: Task): Long {
        val id = taskDao.upsertTask(task)
        val updatedTask = task.copy(id = id)

        supabaseSync?.let {
            try {
                it.syncTaskToSupabase(updatedTask)
            } catch (e: Exception) {
                Log.e("TaskRepository", "Supabase sync failed", e)
            }
        }

        return id
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)

        supabaseSync?.let {
            try {
                it.deleteTaskFromSupabase(task.id)
            } catch (e: Exception) {
                Log.e("TaskRepository", "Supabase delete failed", e)
            }
        }
    }
}
