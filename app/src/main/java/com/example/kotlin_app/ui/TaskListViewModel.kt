package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.Task
import com.example.kotlin_app.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL, COMPLETED, PENDING
}

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL
)

class TaskListViewModel(
    private val courseId: Long,
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            repository.getTasksForCourse(courseId).collectLatest { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }
}

class TaskListViewModelFactory(
    private val courseId: Long,
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(courseId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}

