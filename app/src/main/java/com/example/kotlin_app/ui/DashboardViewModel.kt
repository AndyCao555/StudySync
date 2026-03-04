package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySessionRepository
import com.example.kotlin_app.data.Task
import com.example.kotlin_app.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalCourses: Int = 0,
    val pendingTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalStudyMinutes: Int = 0,
    val upcomingTasks: List<Task> = emptyList(),
    val courseNames: Map<Long, String> = emptyMap()
)

class DashboardViewModel(
    private val courseRepository: CourseRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            courseRepository.getAllCourses().collectLatest { courses ->
                _uiState.value = _uiState.value.copy(
                    totalCourses = courses.size,
                    courseNames = courses.associate { it.id to it.name }
                )
            }
        }
        viewModelScope.launch {
            taskRepository.getAllTasks().collectLatest { tasks ->
                _uiState.value = _uiState.value.copy(
                    pendingTasks = tasks.count { !it.isCompleted },
                    completedTasks = tasks.count { it.isCompleted }
                )
            }
        }
        viewModelScope.launch {
            taskRepository.getUpcomingTasks().collectLatest { tasks ->
                _uiState.value = _uiState.value.copy(upcomingTasks = tasks)
            }
        }
        viewModelScope.launch {
            sessionRepository.getTotalMinutes().collectLatest { total ->
                _uiState.value = _uiState.value.copy(totalStudyMinutes = total ?: 0)
            }
        }
    }
}

class DashboardViewModelFactory(
    private val courseRepository: CourseRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(courseRepository, taskRepository, sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
