package com.example.kotlin_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.data.Course
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySyncDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CourseListUiState(
    val courses: List<Course> = emptyList()
)

class CourseListViewModel(
    private val repository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseListUiState())
    val uiState: StateFlow<CourseListUiState> = _uiState.asStateFlow()

    init {
        observeCourses()
    }

    private fun observeCourses() {
        viewModelScope.launch {
            repository.getAllCourses().collectLatest { courses ->
                _uiState.value = _uiState.value.copy(courses = courses)
            }
        }
    }
}

class CourseListViewModelFactory(
    private val repository: CourseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseListViewModel::class.java)) {
            return CourseListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}

