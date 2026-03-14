package com.example.kotlin_app.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class CourseRepository(
    private val courseDao: CourseDao,
    private val supabaseSync: SupabaseSyncService? = null
) {

    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun upsertCourse(course: Course): Long {
        val id = courseDao.upsertCourse(course)
        val updatedCourse = course.copy(id = id)

        supabaseSync?.let {
            try {
                it.syncCourseToSupabase(updatedCourse)
            } catch (e: Exception) {
                Log.e("CourseRepository", "Supabase sync failed", e)
            }
        }

        return id
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.deleteCourse(course)

        supabaseSync?.let {
            try {
                it.deleteCourseFromSupabase(course.id)
            } catch (e: Exception) {
                Log.e("CourseRepository", "Supabase delete failed", e)
            }
        }
    }
}
