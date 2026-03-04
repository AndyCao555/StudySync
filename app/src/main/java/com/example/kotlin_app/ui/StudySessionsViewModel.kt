package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.Course
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySession
import com.example.kotlin_app.data.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StudySessionsUiState(
    val sessions: List<StudySession> = emptyList(),
    val courses: List<Course> = emptyList(),
    val totalMinutes: Int = 0,
    val showAddDialog: Boolean = false
)

class StudySessionsViewModel(
    private val sessionRepository: StudySessionRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudySessionsUiState())
    val uiState: StateFlow<StudySessionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getAllSessions().collectLatest { sessions ->
                _uiState.value = _uiState.value.copy(sessions = sessions)
            }
        }
        viewModelScope.launch {
            courseRepository.getAllCourses().collectLatest { courses ->
                _uiState.value = _uiState.value.copy(courses = courses)
            }
        }
        viewModelScope.launch {
            sessionRepository.getTotalMinutes().collectLatest { total ->
                _uiState.value = _uiState.value.copy(totalMinutes = total ?: 0)
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun addSession(courseId: Long, date: LocalDate, durationMinutes: Int) {
        viewModelScope.launch {
            sessionRepository.upsertSession(
                StudySession(courseId = courseId, date = date, durationMinutes = durationMinutes)
            )
            hideAddDialog()
        }
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
        }
    }
}

class StudySessionsViewModelFactory(
    private val sessionRepository: StudySessionRepository,
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudySessionsViewModel::class.java)) {
            return StudySessionsViewModel(sessionRepository, courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
