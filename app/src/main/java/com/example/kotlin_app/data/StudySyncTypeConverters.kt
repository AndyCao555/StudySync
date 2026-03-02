package com.example.kotlin_app.data

import androidx.room.TypeConverter
import java.time.LocalDate

class StudySyncTypeConverters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}

