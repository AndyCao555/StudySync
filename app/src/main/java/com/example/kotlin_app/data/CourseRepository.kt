package com.example.kotlin_app.data

import kotlinx.coroutines.flow.Flow

class CourseRepository(
    private val courseDao: CourseDao
) {

    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun upsertCourse(course: Course): Long = courseDao.upsertCourse(course)

    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)
}

