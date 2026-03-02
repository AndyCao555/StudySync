package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.StudySession
import com.example.kotlin_app.data.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class StudySessionsUiState(
    val sessions: List<StudySession> = emptyList(),
    val totalMinutes: Int = 0
)

class StudySessionsViewModel(
    private val courseId: Long,
    private val repository: StudySessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudySessionsUiState())
    val uiState: StateFlow<StudySessionsUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
        observeTotalMinutes()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            repository.getSessionsForCourse(courseId).collectLatest { sessions ->
                _uiState.value = _uiState.value.copy(sessions = sessions)
            }
        }
    }

    private fun observeTotalMinutes() {
        viewModelScope.launch {
            repository.getTotalMinutesForCourse(courseId).collectLatest { total ->
                _uiState.value = _uiState.value.copy(totalMinutes = total ?: 0)
            }
        }
    }
}

class StudySessionsViewModelFactory(
    private val courseId: Long,
    private val repository: StudySessionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudySessionsViewModel::class.java)) {
            return StudySessionsViewModel(courseId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}

