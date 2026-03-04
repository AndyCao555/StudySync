package com.example.kotlin_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySessionRepository
import com.example.kotlin_app.data.StudySyncDatabase
import com.example.kotlin_app.data.Task
import com.example.kotlin_app.data.TaskRepository
import com.example.kotlin_app.ui.DashboardViewModel
import com.example.kotlin_app.ui.DashboardViewModelFactory

@Composable
fun DashboardScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToSessions: () -> Unit
) {
    val context = LocalContext.current
    val database = StudySyncDatabase.getInstance(context)
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            courseRepository = CourseRepository(database.courseDao()),
            taskRepository = TaskRepository(database.taskDao()),
            sessionRepository = StudySessionRepository(database.studySessionDao())
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    val studyHours = uiState.totalStudyMinutes / 60
    val studyMins = uiState.totalStudyMinutes % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "StudySync",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Here's your study overview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stats grid — 2 columns × 2 rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Pending Tasks",
                value = uiState.pendingTasks.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Completed Tasks",
                value = uiState.completedTasks.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Courses",
                value = uiState.totalCourses.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Study Time",
                value = if (studyHours > 0) "${studyHours}h ${studyMins}m" else "${studyMins}m",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming deadlines section
        Text(
            text = "Upcoming Deadlines",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.upcomingTasks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "No upcoming deadlines — you're all caught up!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.upcomingTasks.forEach { task ->
                    UpcomingTaskCard(
                        task = task,
                        courseName = uiState.courseNames[task.courseId] ?: "Unknown Course"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation
        Text(
            text = "Navigate",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToCourses,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Courses & Tasks")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onNavigateToSessions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Study Sessions")
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UpcomingTaskCard(task: Task, courseName: String) {
    val priorityLabel = when (task.priority) {
        3 -> "High"
        2 -> "Medium"
        else -> "Low"
    }
    val priorityColor = when (task.priority) {
        3 -> MaterialTheme.colorScheme.error
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = task.dueDate?.toString() ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = priorityLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
