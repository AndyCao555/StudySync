# How to Test TaskListScreen in Android Studio

## 🚀 Quick Start: Running the App

### Step 1: Open the Project
1. Open Android Studio
2. Open your project: `StudySync` folder
3. Wait for Gradle sync to complete

### Step 2: Set Up an Emulator or Device
**Option A: Use Android Emulator**
1. Click **Device Manager** (phone icon in toolbar)
2. Click **Create Device** (or use existing)
3. Select a device (e.g., Pixel 5)
4. Select a system image (API 34+ recommended)
5. Click **Finish**

**Option B: Use Physical Device**
1. Enable **Developer Options** on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging** in Developer Options
3. Connect phone via USB
4. Allow USB debugging when prompted

### Step 3: Run the App
1. Select your device/emulator from the dropdown (top toolbar)
2. Click the **▶️ Run** button (green play icon)
   - Or press `Shift + F10` (Windows/Linux)
   - Or press `Ctrl + R` (Mac)
3. Wait for the app to build and install
4. The app should launch automatically

---

## 📱 Testing TaskListScreen Step-by-Step

### **Test 1: Navigate to TaskListScreen**

1. **Launch app** → You'll see the Dashboard
2. **Click "Go to Courses"** button
3. **Add a test course**:
   - Enter course name: `"Math 101"`
   - Enter color: `"#2196F3"` (or any hex color)
   - Click **"Add Course"**
4. **Click on the course card** → This navigates to TaskListScreen
5. ✅ **Expected**: You should see "Tasks" header, filter chips, and "No tasks yet" message

---

### **Test 2: Add Tasks (If TaskEditScreen is implemented)**

**Note:** If TaskEditScreen is still a placeholder, you'll need to add tasks manually via code (see "Adding Test Data Manually" below).

1. **Click the FloatingActionButton** (+ button) → Opens TaskEditScreen
2. **Fill in task details**:
   - Title: `"Complete Assignment 1"`
   - Description: `"Finish calculus problems"`
   - Due Date: `"2024-12-20"` (format: YYYY-MM-DD)
   - Priority: `3` (1-5)
   - Completed: Leave unchecked
3. **Click "Save Task"**
4. ✅ **Expected**: Task appears in TaskListScreen

**Repeat** to add 2-3 more tasks with different statuses.

---

### **Test 3: Filter Tasks**

1. **Add multiple tasks** (some completed, some pending)
2. **Test filter chips**:
   - Click **"All"** → Should show all tasks
   - Click **"Pending"** → Should show only incomplete tasks
   - Click **"Completed"** → Should show only completed tasks
3. ✅ **Expected**: List updates immediately when clicking filters

---

### **Test 4: Toggle Task Completion**

1. **Find a pending task** (checkbox unchecked)
2. **Click the checkbox** → Task should be marked as completed
3. ✅ **Expected**: 
   - Checkbox becomes checked
   - Task moves to "Completed" filter when selected
   - Task disappears from "Pending" filter

**Test reverse**:
1. **Click "Completed" filter**
2. **Uncheck a completed task**
3. ✅ **Expected**: Task moves back to "Pending"

---

### **Test 5: Delete Tasks**

1. **Find any task**
2. **Click the delete icon** (trash icon) on the right
3. ✅ **Expected**: Task disappears immediately from the list

---

### **Test 6: Navigate to Edit Task**

1. **Click on a task card** (not the checkbox or delete button)
2. ✅ **Expected**: Navigates to TaskEditScreen (if implemented)

---

### **Test 7: Back Navigation**

1. **Click "Back" button** at bottom
2. ✅ **Expected**: Returns to CourseListScreen

---

## 🛠️ Adding Test Data Manually (If TaskEditScreen Not Ready)

If TaskEditScreen is still a placeholder, you can add test data directly in code:

### **Option 1: Add Test Data in MainActivity (Temporary)**

Add this function to `MainActivity.kt` temporarily:

```kotlin
// Add this function inside MainActivity class
private fun addTestData(context: Context) {
    val database = StudySyncDatabase.getInstance(context)
    val courseDao = database.courseDao()
    val taskDao = database.taskDao()
    
    lifecycleScope.launch {
        // Add a course
        val courseId = courseDao.upsertCourse(
            Course(name = "Math 101", colorHex = "#2196F3")
        )
        
        // Add test tasks
        taskDao.upsertTask(
            Task(
                courseId = courseId,
                title = "Assignment 1",
                description = "Complete calculus problems",
                dueDate = LocalDate.now().plusDays(5),
                priority = 3,
                isCompleted = false
            )
        )
        
        taskDao.upsertTask(
            Task(
                courseId = courseId,
                title = "Midterm Exam",
                description = "Study chapters 1-5",
                dueDate = LocalDate.now().plusDays(10),
                priority = 5,
                isCompleted = false
            )
        )
        
        taskDao.upsertTask(
            Task(
                courseId = courseId,
                title = "Homework 3",
                description = "Already completed",
                dueDate = LocalDate.now().minusDays(2),
                priority = 2,
                isCompleted = true
            )
        )
    }
}
```

Then call it in `onCreate()`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    addTestData(this) // Add this line
    setContent {
        // ...
    }
}
```

**Remember to remove this code after testing!**

---

### **Option 2: Use Database Inspector**

1. **Run the app**
2. **In Android Studio**: View → Tool Windows → **App Inspection**
3. **Select your app** from dropdown
4. **Click "Database Inspector"** tab
5. **Find `studysync.db`** → Expand → `tasks` table
6. **Click "+"** to add rows manually
7. Fill in:
   - `courseId`: 1 (or your course ID)
   - `title`: "Test Task"
   - `description`: "Test description"
   - `dueDate`: "2024-12-20"
   - `priority`: 3
   - `isCompleted`: 0 (false) or 1 (true)
8. **Click "✓"** to save
9. **Go back to app** → Task should appear!

---

## ✅ Checklist: What to Verify

- [ ] App launches without crashes
- [ ] Can navigate to TaskListScreen from CourseListScreen
- [ ] Filter chips appear (All/Pending/Completed)
- [ ] Tasks display in LazyColumn (if any exist)
- [ ] Empty state message shows when no tasks
- [ ] Filtering works correctly
- [ ] Checkbox toggles completion status
- [ ] Delete button removes tasks
- [ ] FloatingActionButton appears
- [ ] Back button navigates correctly
- [ ] UI updates reactively when data changes

---

## 🐛 Common Issues & Solutions

### **Issue: App crashes on launch**
- **Solution**: Check Logcat for errors. Common causes:
  - Missing imports
  - Database not initialized
  - ViewModel factory issues

### **Issue: Tasks don't appear**
- **Solution**: 
  - Check if tasks exist in database (use Database Inspector)
  - Verify `courseId` matches
  - Check Logcat for database errors

### **Issue: Filter doesn't work**
- **Solution**: 
  - Verify `filteredTasks` property in ViewModel
  - Check that filter state updates correctly

### **Issue: Checkbox/Delete doesn't work**
- **Solution**:
  - Check Logcat for coroutine errors
  - Verify repository methods are called
  - Check database permissions

---

## 📊 Using Logcat for Debugging

1. **Open Logcat**: View → Tool Windows → **Logcat**
2. **Filter by tag**: Type `TaskListViewModel` or `TaskRepository`
3. **Look for errors**: Red text indicates issues
4. **Add logging** (optional):
   ```kotlin
   Log.d("TaskListScreen", "Filtered tasks: ${filteredTasks.size}")
   ```

---

## 🎯 Quick Test Script

**Fastest way to test everything:**

1. ✅ Run app
2. ✅ Add course "Test Course"
3. ✅ Click course → See empty TaskListScreen
4. ✅ Add 3 tasks via TaskEditScreen (or manually)
5. ✅ Test filters: All → Pending → Completed
6. ✅ Toggle one task complete
7. ✅ Delete one task
8. ✅ Click task card → Navigate to edit
9. ✅ Click back → Return to courses

**If all steps work → TaskListScreen is working! 🎉**

---

## 💡 Pro Tips

1. **Use Preview**: You can preview composables in Android Studio
   - Click the "Design" tab next to code
   - Or use `@Preview` annotation

2. **Database Inspector**: Great for checking data without UI
   - View → Tool Windows → App Inspection → Database Inspector

3. **Breakpoints**: Set breakpoints in ViewModel to debug
   - Click left margin to add breakpoint
   - Run in debug mode (🐛 icon)

4. **Clear Data**: If database gets messy
   - Settings → Apps → Your App → Storage → Clear Data
   - Or uninstall/reinstall app

---

Good luck testing! 🚀
