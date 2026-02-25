## StudySync – Navigation Skeleton

This project currently contains a **barebones navigation setup** for the StudySync app, implemented with Kotlin and Jetpack Compose.

### What was added

- **Navigation dependency**
  - In `app/build.gradle.kts`, the dependency  
    `implementation("androidx.navigation:navigation-compose:2.8.0")`  
    was added so the app can use Jetpack Compose Navigation.

- **`StudySyncApp` composable**
  - Defined in `MainActivity.kt`.
  - Creates a `NavHost` with a `rememberNavController()` and sets the **start destination** to `"dashboard"`.
  - This is where all StudySync screens are registered as navigation routes.

- **Navigation routes**
  - `dashboard` – entry point of the app.
  - `courses` – course list screen.
  - `tasks/{courseId}` – task list for a specific course (uses a `courseId` argument).
  - `taskEdit/{courseId}` – add a new task for a course.
  - `taskEdit/{courseId}/{taskId}` – edit an existing task for a course.
  - `sessions` – study sessions screen.
  - These routes demonstrate **argument passing and back stack handling**, which will later be backed by real data.

- **Placeholder screens (all in `MainActivity.kt` for now)**
  - `DashboardScreen` – shows simple text and buttons to navigate to Courses or Study Sessions.
  - `CourseListScreen` – placeholder for a course list (later will use `LazyColumn` + Room).
  - `TaskListScreen` – placeholder for tasks for a given `courseId`, with buttons to “add task” or open an example task.
  - `TaskEditScreen` – placeholder for adding/editing a task, showing the `courseId` and optional `taskId`.
  - `StudySessionsScreen` – placeholder where scheduled sessions and total study time will go.
  - Each screen is kept minimal (mostly `Text` + `Button`) so you can run the app and click through the full flow before adding real logic.

- **`MainActivity`**
  - Now simply calls `StudySyncApp()` inside `setContent { Kotlin_AppTheme { ... } }`.
  - This keeps `MainActivity` focused on bootstrapping Compose, while the app flow lives in `StudySyncApp`.

### Next steps (for later)

- Replace placeholder screens with real UI:
  - Use `LazyColumn` for courses, tasks, and study sessions.
  - Add ViewModels and Room entities (`Course`, `Task`, `StudySession`) for local persistence.
- Add Firebase Firestore integration and (optionally) WorkManager for background sync.
- Move each screen into its own file/package (e.g. `ui/dashboard`, `ui/courses`, `ui/tasks`, `ui/sessions`) to follow a clean MVVM structure.


