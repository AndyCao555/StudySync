package com.example.kotlin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_app.data.Course
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySyncDatabase
import com.example.kotlin_app.data.SupabaseSyncService
import com.example.kotlin_app.data.Task
import com.example.kotlin_app.data.TaskRepository
import java.time.format.DateTimeFormatter
import com.example.kotlin_app.ui.CourseListViewModel
import com.example.kotlin_app.ui.CourseListViewModelFactory
import com.example.kotlin_app.ui.TaskListViewModel
import com.example.kotlin_app.ui.TaskListViewModelFactory
import com.example.kotlin_app.ui.TaskFilter
import com.example.kotlin_app.ui.theme.Kotlin_AppTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_AppTheme {
                StudySyncApp()
            }
        }
    }
}

@Composable
fun StudySyncApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "dashboard") {
                DashboardScreen(
                    onNavigateToCourses = { navController.navigate("courses") },
                    onNavigateToSessions = { navController.navigate("sessions") }
                )
            }

            composable(route = "courses") {
                CourseListScreen(
                    onBack = { navController.popBackStack() },
                    onCourseSelected = { courseId ->
                        navController.navigate("tasks/$courseId")
                    }
                )
            }

            composable(route = "tasks/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: "unknown"
                val context = LocalContext.current
                val database = StudySyncDatabase.getInstance(context)
                val supabaseSync = SupabaseSyncService()
                val taskRepository = TaskRepository(database.taskDao(), supabaseSync)
                val courseIdLong = courseId.toLongOrNull() ?: 0L
                
                TaskListScreen(
                    courseId = courseId,
                    courseIdLong = courseIdLong,
                    taskRepository = taskRepository,
                    onBack = { navController.popBackStack() },
                    onAddTask = {
                        navController.navigate("taskEdit/$courseId")
                    },
                    onTaskSelected = { taskId ->
                        navController.navigate("taskEdit/$courseId/$taskId")
                    }
                )
            }

            composable(route = "taskEdit/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: "unknown"
                TaskEditScreen(
                    courseId = courseId,
                    taskId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(route = "taskEdit/{courseId}/{taskId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: "unknown"
                val taskId = backStackEntry.arguments?.getString("taskId")
                TaskEditScreen(
                    courseId = courseId,
                    taskId = taskId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(route = "sessions") {
                StudySessionsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudySyncAppPreview() {
    Kotlin_AppTheme {
        StudySyncApp()
    }
}

/**
 * Simple placeholder for the dashboard screen.
 * This will later show stats like pending tasks and study hours.
 */
@Composable
fun DashboardScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToSessions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "StudySync Dashboard")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToCourses) {
            Text(text = "Go to Courses")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToSessions) {
            Text(text = "Go to Study Sessions")
        }
    }
}

/**
 * Course list screen backed by Room + ViewModel.
 */
@Composable
fun CourseListScreen(
    onBack: () -> Unit,
    onCourseSelected: (courseId: String) -> Unit
) {
    val context = LocalContext.current

    // Simple manual wiring of the database, repository, and ViewModel.
    val database = StudySyncDatabase.getInstance(context)
    val supabaseSync = SupabaseSyncService()
    val repository = CourseRepository(database.courseDao(), supabaseSync)

    val viewModel: CourseListViewModel = viewModel(
        factory = CourseListViewModelFactory(repository)
    )

    val uiState by viewModel.uiState.collectAsState()

    val (newCourseName, setNewCourseName) = remember { mutableStateOf("") }
    val (newCourseColor, setNewCourseColor) = remember { mutableStateOf("#2196F3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Courses",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newCourseName,
            onValueChange = setNewCourseName,
            label = { Text("Course name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newCourseColor,
            onValueChange = setNewCourseColor,
            label = { Text("Color (hex)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.addCourse(newCourseName, newCourseColor)
                setNewCourseName("")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = newCourseName.isNotBlank()
        ) {
            Text("Add Course")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.courses.isEmpty()) {
            Text(
                text = "No courses yet. You’ll add creation UI next.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.courses, key = Course::id) { course ->
                    CourseRow(
                        course = course,
                        onClick = { onCourseSelected(course.id.toString()) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Back")
        }
    }
}

@Composable
private fun CourseRow(
    course: Course,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
            .padding(16.dp)
        ) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Color: ${course.colorHex}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Task list screen with filtering and task management.
 */
@Composable
fun TaskListScreen(
    courseId: String,
    courseIdLong: Long,
    taskRepository: TaskRepository,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onTaskSelected: (taskId: String) -> Unit
) {
    if (courseIdLong == 0L) {
        // Invalid courseId - show error and back button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Invalid course ID")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
        return
    }

    val viewModel: TaskListViewModel = viewModel(
        key = "task_list_$courseIdLong",
        factory = TaskListViewModelFactory(courseIdLong, taskRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    
    // Compute filtered tasks reactively (no remember needed - computed on each recomposition)
    val filteredTasks = when (uiState.filter) {
        TaskFilter.ALL -> uiState.tasks
        TaskFilter.COMPLETED -> uiState.tasks.filter { it.isCompleted }
        TaskFilter.PENDING -> uiState.tasks.filter { !it.isCompleted }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filter == TaskFilter.ALL,
                    onClick = { viewModel.setFilter(TaskFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.filter == TaskFilter.PENDING,
                    onClick = { viewModel.setFilter(TaskFilter.PENDING) },
                    label = { Text("Pending") }
                )
                FilterChip(
                    selected = uiState.filter == TaskFilter.COMPLETED,
                    onClick = { viewModel.setFilter(TaskFilter.COMPLETED) },
                    label = { Text("Completed") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTasks.isEmpty()) {
                Text(
                    text = when (uiState.filter) {
                        TaskFilter.ALL -> "No tasks yet"
                        TaskFilter.COMPLETED -> "No completed tasks"
                        TaskFilter.PENDING -> "No pending tasks"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks, key = Task::id) { task ->
                        TaskRow(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onClick = { onTaskSelected(task.id.toString()) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Back")
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox with proper click handling
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { 
                    onToggleComplete()
                },
                modifier = Modifier.clickable { onToggleComplete() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    task.dueDate?.let {
                        Text(
                            text = "Due: ${it.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "Priority: ${task.priority}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            // Delete button with proper click handling
            IconButton(
                onClick = onDelete,
                modifier = Modifier
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

/**
 * Task edit screen for adding or editing tasks.
 */
@Composable
fun TaskEditScreen(
    courseId: String,
    taskId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = StudySyncDatabase.getInstance(context)
    val supabaseSync = SupabaseSyncService()
    val taskRepository = TaskRepository(database.taskDao(), supabaseSync)
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDateText by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("3") }
    var isCompleted by remember { mutableStateOf(false) }

    // Input/output formatter for the date field: DD-MM-YYYY
    val dateInputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    // Load existing task if editing
    LaunchedEffect(taskId) {
        taskId?.toLongOrNull()?.let { id ->
            try {
                val task = taskRepository.getTaskById(id).first()
                task?.let {
                    title = it.title
                    description = it.description ?: ""
                    dueDateText = it.dueDate?.format(dateInputFormatter) ?: ""
                    priority = it.priority.toString()
                    isCompleted = it.isCompleted
                }
            } catch (e: Exception) {
                // Task not found or error loading - stay with empty form
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (taskId == null) "Add Task" else "Edit Task",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dueDateText,
            onValueChange = { newValue ->
                // Only allow numbers and dashes
                if (newValue.all { it.isDigit() || it == '-' }) {
                    dueDateText = newValue
                }
            },
            label = { Text("Due Date (DD-MM-YYYY)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("31-12-2024") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = priority,
            onValueChange = { newValue ->
                // Only allow numbers, and limit to single digit (1-5)
                if (newValue.all { it.isDigit() } && newValue.length <= 1) {
                    val num = newValue.toIntOrNull()
                    if (num == null || (num in 1..5)) {
                        priority = newValue
                    }
                } else if (newValue.isEmpty()) {
                    priority = newValue
                }
            },
            label = { Text("Priority (1-5)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { isCompleted = it }
            )
            Text("Completed")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val dueDate = try {
                    if (dueDateText.isNotBlank()) LocalDate.parse(dueDateText, dateInputFormatter) else null
                } catch (e: Exception) {
                    null
                }

                val task = Task(
                    id = taskId?.toLongOrNull() ?: 0L,
                    courseId = courseId.toLongOrNull() ?: 0L,
                    title = title,
                    description = description.ifBlank { null },
                    dueDate = dueDate,
                    priority = priority.toIntOrNull()?.coerceIn(1, 5) ?: 3,
                    isCompleted = isCompleted
                )

                coroutineScope.launch {
                    taskRepository.upsertTask(task)
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text(if (taskId == null) "Save Task" else "Update Task")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

/**
 * Placeholder for the study sessions screen.
 * Will later show scheduled sessions and total study time.
 */
@Composable
fun StudySessionsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Study Sessions")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}