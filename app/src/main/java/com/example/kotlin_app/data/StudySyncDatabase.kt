package com.example.kotlin_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Course::class, Task::class, StudySession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StudySyncTypeConverters::class)
abstract class StudySyncDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: StudySyncDatabase? = null

        fun getInstance(context: Context): StudySyncDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudySyncDatabase::class.java,
                    "studysync.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

