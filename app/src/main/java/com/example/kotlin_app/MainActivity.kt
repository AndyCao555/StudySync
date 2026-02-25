package com.example.kotlin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_app.ui.theme.Kotlin_AppTheme

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
                TaskListScreen(
                    courseId = courseId,
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
 * Placeholder for the course list screen.
 * Later this will show a LazyColumn of courses from Room.
 */
@Composable
fun CourseListScreen(
    onBack: () -> Unit,
    onCourseSelected: (courseId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Course List")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onCourseSelected("exampleCourseId") }) {
            Text(text = "Open Example Course")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}

/**
 * Placeholder for the task list for a given course.
 * Will later display and filter tasks for the selected course.
 */
@Composable
fun TaskListScreen(
    courseId: String,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onTaskSelected: (taskId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tasks for course: $courseId")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddTask) {
            Text(text = "Add Task")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onTaskSelected("exampleTaskId") }) {
            Text(text = "Open Example Task")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}

/**
 * Placeholder for adding or editing a task.
 * Will later contain a full task form with fields like title, due date, etc.
 */
@Composable
fun TaskEditScreen(
    courseId: String,
    taskId: String?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Task for course: $courseId")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Task ID: ${taskId ?: "new task"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Back")
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