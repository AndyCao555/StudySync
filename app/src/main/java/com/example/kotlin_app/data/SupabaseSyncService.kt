package com.example.kotlin_app.data

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.JsonArray
import java.time.Instant
import java.util.UUID

/**
 * Service to sync data between Room (local) and Supabase (cloud).
 * Uses separate tables from flask-app: studysync_courses, studysync_tasks, studysync_study_sessions
 */
class SupabaseSyncService {
    private val client = SupabaseClient.client
    
    // ========== Courses ==========
    
    suspend fun syncCourseToSupabase(course: Course) {
        val client = this.client ?: return // Skip if Supabase not available
        
        try {
            val supabaseCourse = SupabaseCourse(
                id = course.id,
                name = course.name,
                color_hex = course.colorHex
            )
            
            android.util.Log.d("SupabaseSyncService", "Syncing course: id=${course.id}, name=${course.name}")
            
            // Check if course exists, then insert or update accordingly
            val existing = try {
                client.from("studysync_courses")
                    .select { filter { eq("id", course.id) } }
                    .decodeSingleOrNull<SupabaseCourse>()
            } catch (e: Exception) {
                null
            }
            
            if (existing != null) {
                // Update existing
                client.from("studysync_courses").update(supabaseCourse) {
                    filter { eq("id", course.id) }
                }
                android.util.Log.d("SupabaseSyncService", "Course updated: id=${course.id}")
            } else {
                // Insert new
                client.from("studysync_courses").insert(supabaseCourse)
                android.util.Log.d("SupabaseSyncService", "Course inserted: id=${course.id}")
            }
            
            android.util.Log.d("SupabaseSyncService", "Course synced successfully: id=${course.id}")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSyncService", "Failed to sync course ${course.id} (${course.name})", e)
            android.util.Log.e("SupabaseSyncService", "Error message: ${e.message}")
            e.printStackTrace()
            throw e // Re-throw to be caught by repository
        }
    }
    
    suspend fun getAllCoursesFromSupabase(): List<Course> {
        val client = this.client ?: return emptyList()
        return try {
            client.from("studysync_courses")
                .select()
                .decodeList<SupabaseCourse>()
                .map { it.toCourse() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun deleteCourseFromSupabase(courseId: Long) {
        val client = this.client ?: return
        client.from("studysync_courses").delete {
            filter { eq("id", courseId) }
        }
    }
    
    // ========== Tasks ==========
    
    suspend fun syncTaskToSupabase(task: Task) {
        val client = this.client ?: run {
            android.util.Log.w("SupabaseSyncService", "Supabase client is null - sync skipped for task: ${task.title}")
            return
        }
        
        try {
            val supabaseTask = SupabaseTask(
                id = task.id,
                course_id = task.courseId,
                title = task.title,
                description = task.description,
                due_date = task.dueDate?.toString(),
                priority = task.priority,
                is_completed = task.isCompleted
            )
            
            android.util.Log.d("SupabaseSyncService", "Syncing task: id=${task.id}, title=${task.title}, course_id=${task.courseId}")
            
            // Check if task exists, then insert or update accordingly
            val existing = try {
                client.from("studysync_tasks")
                    .select { filter { eq("id", task.id) } }
                    .decodeSingleOrNull<SupabaseTask>()
            } catch (e: Exception) {
                null
            }
            
            if (existing != null) {
                // Update existing
                client.from("studysync_tasks").update(supabaseTask) {
                    filter { eq("id", task.id) }
                }
                android.util.Log.d("SupabaseSyncService", "Task updated: id=${task.id}")
            } else {
                // Insert new
                client.from("studysync_tasks").insert(supabaseTask)
                android.util.Log.d("SupabaseSyncService", "Task inserted: id=${task.id}")
            }
            
            android.util.Log.d("SupabaseSyncService", "Task synced successfully: id=${task.id}")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSyncService", "Failed to sync task ${task.id} (${task.title})", e)
            android.util.Log.e("SupabaseSyncService", "Error message: ${e.message}")
            e.printStackTrace()
            throw e // Re-throw to be caught by repository
        }
    }
    
    suspend fun getTasksForCourseFromSupabase(courseId: Long): List<Task> {
        val client = this.client ?: return emptyList()
        return try {
            client.from("studysync_tasks")
                .select {
                    filter { eq("course_id", courseId) }
                }
                .decodeList<SupabaseTask>()
                .map { it.toTask() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun deleteTaskFromSupabase(taskId: Long) {
        val client = this.client ?: run {
            android.util.Log.w("SupabaseSyncService", "Supabase client is null - delete skipped for task: $taskId")
            return
        }
        
        try {
            android.util.Log.d("SupabaseSyncService", "Deleting task from Supabase: id=$taskId")
            client.from("studysync_tasks").delete {
                filter { eq("id", taskId) }
            }
            android.util.Log.d("SupabaseSyncService", "Task deleted successfully from Supabase: id=$taskId")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSyncService", "Failed to delete task $taskId from Supabase", e)
            android.util.Log.e("SupabaseSyncService", "Error message: ${e.message}")
            e.printStackTrace()
            throw e // Re-throw to be caught by repository
        }
    }
    
    // ========== Study Sessions ==========
    
    suspend fun syncStudySessionToSupabase(session: StudySession) {
        val client = this.client ?: run {
            android.util.Log.w("SupabaseSyncService", "Supabase client is null - sync skipped for session: ${session.id}")
            return
        }
        
        try {
            val supabaseSession = SupabaseStudySession(
                id = session.id,
                course_id = session.courseId,
                date = session.date.toString(),
                duration_minutes = session.durationMinutes
            )
            
            android.util.Log.d("SupabaseSyncService", "Syncing study session: id=${session.id}, course_id=${session.courseId}")
            
            // Check if session exists, then insert or update accordingly
            val existing = try {
                client.from("studysync_study_sessions")
                    .select { filter { eq("id", session.id) } }
                    .decodeSingleOrNull<SupabaseStudySession>()
            } catch (e: Exception) {
                null
            }
            
            if (existing != null) {
                client.from("studysync_study_sessions").update(supabaseSession) {
                    filter { eq("id", session.id) }
                }
                android.util.Log.d("SupabaseSyncService", "Study session updated: id=${session.id}")
            } else {
                client.from("studysync_study_sessions").insert(supabaseSession)
                android.util.Log.d("SupabaseSyncService", "Study session inserted: id=${session.id}")
            }
            
            android.util.Log.d("SupabaseSyncService", "Study session synced successfully: id=${session.id}")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSyncService", "Failed to sync study session ${session.id}", e)
            android.util.Log.e("SupabaseSyncService", "Error message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun getSessionsForCourseFromSupabase(courseId: Long): List<StudySession> {
        val client = this.client ?: return emptyList()
        return try {
            client.from("studysync_study_sessions")
                .select {
                    filter { eq("course_id", courseId) }
                }
                .decodeList<SupabaseStudySession>()
                .map { it.toStudySession() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun deleteStudySessionFromSupabase(sessionId: Long) {
        val client = this.client ?: return
        client.from("studysync_study_sessions").delete {
            filter { eq("id", sessionId) }
        }
    }
    
    // ========== Create Snapshot for Collector Agent ==========
    
    /**
     * Creates a snapshot document that the collector agent can read.
     * Stores in studysync_snapshots table (separate from flask-app tables).
     */
    suspend fun createSnapshotForCollector(
        courses: List<Course>,
        tasks: List<Task>,
        studySessions: List<StudySession>,
        deviceId: String = android.os.Build.MODEL,
        aggregatorId: String = UUID.randomUUID().toString()
    ) {
        val timestamp = Instant.now().toString()
        val snapshotId = "${timestamp}_${UUID.randomUUID().toString().take(8)}"
        
        val payload = buildJsonObject {
            putJsonArray("courses") {
                courses.forEach { course ->
                    val courseMap = courseToMap(course)
                    add(buildJsonObject {
                        courseMap.forEach { (key, value) ->
                            when (value) {
                                is String -> put(key, value)
                                is Number -> put(key, value)
                                is Boolean -> put(key, value)
                                else -> put(key, value.toString())
                            }
                        }
                    })
                }
            }
            putJsonArray("tasks") {
                tasks.forEach { task ->
                    val taskMap = taskToMap(task)
                    add(buildJsonObject {
                        taskMap.forEach { (key, value) ->
                            when (value) {
                                is String -> put(key, value)
                                is Number -> put(key, value)
                                is Boolean -> put(key, value)
                                else -> put(key, value.toString())
                            }
                        }
                    })
                }
            }
            putJsonArray("study_sessions") {
                studySessions.forEach { session ->
                    val sessionMap = sessionToMap(session)
                    add(buildJsonObject {
                        sessionMap.forEach { (key, value) ->
                            when (value) {
                                is String -> put(key, value)
                                is Number -> put(key, value)
                                is Boolean -> put(key, value)
                                else -> put(key, value.toString())
                            }
                        }
                    })
                }
            }
            put("stats", buildJsonObject {
                put("total_courses", courses.size)
                put("total_tasks", tasks.size)
                put("pending_tasks", tasks.count { !it.isCompleted })
                put("completed_tasks", tasks.count { it.isCompleted })
                put("total_study_hours", studySessions.sumOf { it.durationMinutes } / 60.0)
            })
        }
        
        val client = this.client ?: return
        
        val snapshot = SupabaseSnapshot(
            id = snapshotId,
            timestamp = timestamp,
            device = mapOf("name" to deviceId),
            aggregator = mapOf(
                "uuid" to aggregatorId,
                "name" to "StudySync App"
            ),
            payload = payload
        )
        
        client.from("studysync_snapshots").insert(snapshot)
    }
    
    private fun courseToMap(course: Course): Map<String, Any> {
        return mapOf(
            "id" to course.id,
            "name" to course.name,
            "color_hex" to course.colorHex
        )
    }
    
    private fun taskToMap(task: Task): Map<String, Any> {
        return mapOf(
            "id" to task.id,
            "course_id" to task.courseId,
            "title" to task.title,
            "description" to (task.description ?: ""),
            "due_date" to (task.dueDate?.toString() ?: ""),
            "priority" to task.priority,
            "is_completed" to task.isCompleted
        )
    }
    
    private fun sessionToMap(session: StudySession): Map<String, Any> {
        return mapOf(
            "id" to session.id,
            "course_id" to session.courseId,
            "date" to session.date.toString(),
            "duration_minutes" to session.durationMinutes
        )
    }
    
}

// ========== Data Classes for Supabase ==========

@Serializable
data class SupabaseCourse(
    val id: Long,
    val name: String,
    val color_hex: String
) {
    fun toCourse() = Course(id = id, name = name, colorHex = color_hex)
}

@Serializable
data class SupabaseTask(
    val id: Long,
    val course_id: Long,
    val title: String,
    val description: String?,
    val due_date: String?,
    val priority: Int,
    val is_completed: Boolean
) {
    fun toTask() = Task(
        id = id,
        courseId = course_id,
        title = title,
        description = description,
        dueDate = due_date?.let { java.time.LocalDate.parse(it) },
        priority = priority,
        isCompleted = is_completed
    )
}

@Serializable
data class SupabaseStudySession(
    val id: Long,
    val course_id: Long,
    val date: String,
    val duration_minutes: Int
) {
    fun toStudySession() = StudySession(
        id = id,
        courseId = course_id,
        date = java.time.LocalDate.parse(date),
        durationMinutes = duration_minutes
    )
}

@Serializable
data class SupabaseSnapshot(
    val id: String,
    val timestamp: String,
    val device: Map<String, String>,
    val aggregator: Map<String, String>,
    val payload: JsonObject
)
