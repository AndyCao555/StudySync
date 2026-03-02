package com.example.kotlin_app.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getTasksForCourse(courseId: Long): Flow<List<Task>> =
        taskDao.getTasksForCourse(courseId)

    fun getTaskById(taskId: Long): Flow<Task?> =
        taskDao.getTaskById(taskId)

    suspend fun upsertTask(task: Task): Long = taskDao.upsertTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}

