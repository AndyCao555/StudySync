package com.example.kotlin_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_app.data.Course
import com.example.kotlin_app.data.CourseRepository
import com.example.kotlin_app.data.StudySession
import com.example.kotlin_app.data.StudySessionRepository
import com.example.kotlin_app.data.StudySyncDatabase
import com.example.kotlin_app.ui.StudySessionsViewModel
import com.example.kotlin_app.ui.StudySessionsViewModelFactory
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = StudySyncDatabase.getInstance(context)
    val viewModel: StudySessionsViewModel = viewModel(
        factory = StudySessionsViewModelFactory(
            sessionRepository = StudySessionRepository(database.studySessionDao()),
            courseRepository = CourseRepository(database.courseDao())
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showAddDialog) {
        AddSessionDialog(
            courses = uiState.courses,
            onDismiss = viewModel::hideAddDialog,
            onConfirm = { courseId, date, duration ->
                viewModel.addSession(courseId, date, duration)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Study Sessions",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total study time summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Total Study Time",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                val hours = uiState.totalMinutes / 60
                val minutes = uiState.totalMinutes % 60
                Text(
                    text = "${hours}h ${minutes}m",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::showAddDialog,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.courses.isNotEmpty()
        ) {
            Text(if (uiState.courses.isEmpty()) "Add a course first" else "Add Study Session")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.courses.isEmpty())
                        "No courses yet. Go to Courses to add one."
                    else
                        "No sessions yet. Tap 'Add Study Session' to log one.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.sessions, key = { it.id }) { session ->
                    SessionCard(
                        session = session,
                        courseName = uiState.courses.find { it.id == session.courseId }?.name
                            ?: "Unknown Course",
                        onDelete = { viewModel.deleteSession(session) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun SessionCard(
    session: StudySession,
    courseName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = session.date.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                val hours = session.durationMinutes / 60
                val mins = session.durationMinutes % 60
                Text(
                    text = if (hours > 0) "${hours}h ${mins}m" else "${mins}m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Remove")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSessionDialog(
    courses: List<Course>,
    onDismiss: () -> Unit,
    onConfirm: (courseId: Long, date: LocalDate, durationMinutes: Int) -> Unit
) {
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()) }
    var dateText by remember { mutableStateOf(LocalDate.now().toString()) }
    var durationText by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }
    var durationError by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Study Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = courseExpanded,
                    onExpandedChange = { courseExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCourse?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = courseExpanded,
                        onDismissRequest = { courseExpanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.name) },
                                onClick = {
                                    selectedCourse = course
                                    courseExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateText,
                    onValueChange = {
                        dateText = it
                        dateError = false
                    },
                    label = { Text("Date (YYYY-MM-DD)") },
                    isError = dateError,
                    supportingText = if (dateError) {
                        { Text("Enter a valid date, e.g. 2025-03-15") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = {
                        durationText = it
                        durationError = false
                    },
                    label = { Text("Duration (minutes)") },
                    isError = durationError,
                    supportingText = if (durationError) {
                        { Text("Enter a number greater than 0") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val date = try { LocalDate.parse(dateText) } catch (e: Exception) { null }
                    val duration = durationText.toIntOrNull()?.takeIf { it > 0 }
                    dateError = date == null
                    durationError = duration == null
                    if (date != null && duration != null && selectedCourse != null) {
                        onConfirm(selectedCourse!!.id, date, duration)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
