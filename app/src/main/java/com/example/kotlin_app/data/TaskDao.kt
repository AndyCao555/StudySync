package com.example.kotlin_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY dueDate IS NULL, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate IS NOT NULL ORDER BY dueDate ASC LIMIT 5")
    fun getUpcomingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE courseId = :courseId ORDER BY dueDate IS NULL, dueDate ASC")
    fun getTasksForCourse(courseId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)
}

