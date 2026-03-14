package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.Task
import com.example.kotlin_app.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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

    private val _filter = MutableStateFlow(TaskFilter.ALL)

    val tasks: StateFlow<List<Task>> = repository.getTasksForCourse(courseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<TaskListUiState> = kotlinx.coroutines.flow.combine(
        tasks,
        _filter
    ) { taskList, filter ->
        TaskListUiState(tasks = taskList, filter = filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskListUiState()
    )

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                id = task.id,
                isCompleted = !task.isCompleted
            )
            repository.upsertTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
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
