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
        
        // Sync to Supabase
        supabaseSync?.let {
            try {
                Log.d("CourseRepository", "Attempting to sync course to Supabase: ${updatedCourse.name}")
                it.syncCourseToSupabase(updatedCourse)
                Log.d("CourseRepository", "Course synced successfully to Supabase")
            } catch (e: Exception) {
                Log.e("CourseRepository", "Supabase sync failed", e)
                Log.e("CourseRepository", "Error details: ${e.message}", e)
            }
        } ?: Log.w("CourseRepository", "SupabaseSyncService is null - sync skipped")
        
        return id
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.deleteCourse(course)
        
        // Delete from Supabase
        supabaseSync?.let {
            try {
                it.deleteCourseFromSupabase(course.id)
            } catch (e: Exception) {
                Log.e("CourseRepository", "Supabase delete failed", e)
            }
        }
    }
}

