# Database Guide - StudySync Android App

## How It Works (Simple)

```
User adds data in app
  ↓
Room Database (local, on phone) - Saves instantly
  ↓
Supabase (cloud) - Syncs automatically in background
```

**That's it!** The app saves locally first (works offline), then syncs to the cloud automatically.

---

## Setup (One Time)

### 1. Get Supabase Credentials

1. Go to [Supabase Dashboard](https://supabase.com/dashboard)
2. Select your project
3. Go to **Settings** → **API**
4. Copy:
   - **Project URL** (e.g., `https://xxxxx.supabase.co`)
   - **anon public key** (long string starting with `eyJ...`)

### 2. Add Credentials to App

1. Open `local.properties` in project root
2. Add:
   ```properties
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=your-anon-key-here
   ```
3. Replace with your actual values from Step 1

### 3. Create Tables in Supabase

1. Go to Supabase Dashboard → **SQL Editor**
2. Copy the entire contents of `supabase/migrations/20250203000000_studysync_initial_schema.sql`
3. Paste and run it
4. ✅ Tables created!

**Tables created:**
- `studysync_courses` - Stores courses
- `studysync_tasks` - Stores tasks
- `studysync_study_sessions` - Stores study sessions
- `studysync_snapshots` - For collector agent

### 4. Sync Gradle & Rebuild

1. In Android Studio: **File → Sync Project with Gradle Files**
2. **Build → Rebuild Project**

---

## How Data Flows

### When You Add a Course

```
1. User clicks "Add Course" → "Math 101"
   ↓
2. CourseRepository.upsertCourse() called
   ↓
3. Room Database saves locally (instant, works offline)
   ↓
4. SupabaseSyncService syncs to cloud (background)
   ↓
5. Data appears in Supabase Dashboard!
```

**Same flow for tasks and study sessions!**

---

## Task Creation Flow (Detailed)

When a user creates a task in the app, here's exactly what happens:

### 1. UI Layer - User Creates Task
**File:** `MainActivity.kt` (`TaskEditScreen` composable)
- User fills out form (title, description, due date, priority)
- Clicks "Save Task" button
- Creates `Task` object and calls `taskRepository.upsertTask(task)`

### 2. Repository Layer - Saves Locally & Syncs
**File:** `TaskRepository.kt`
- `upsertTask(task)` method:
  1. Saves to Room database via `taskDao.upsertTask(task)` (instant, works offline)
  2. Gets generated ID from Room
  3. Calls `supabaseSync.syncTaskToSupabase(updatedTask)` (background sync)
  4. Logs success/failure

### 3. Sync Service Layer - Handles Supabase Communication
**File:** `SupabaseSyncService.kt`
- `syncTaskToSupabase(task)` method:
  1. Converts `Task` → `SupabaseTask` (matches Supabase table schema)
  2. Checks if task exists in Supabase
  3. Inserts if new, updates if existing
  4. Logs detailed sync information

### 4. Supabase Client Layer - HTTP Communication
**File:** `SupabaseClient.kt`
- Initializes Supabase client with credentials from `BuildConfig`
- Provides client used by `SupabaseSyncService`

### Files Involved

1. **`MainActivity.kt`** - UI (`TaskEditScreen`)
2. **`TaskRepository.kt`** - Coordinates local save + Supabase sync
3. **`SupabaseSyncService.kt`** - Handles Supabase API calls
4. **`SupabaseClient.kt`** - Initializes Supabase connection
5. **`Task.kt`** - Data model
6. **`TaskDao.kt`** - Room database access (saves locally)

**Flow:** UI → Repository → Sync Service → Supabase API → Database

*Same pattern applies to courses and study sessions!*

---

## What Gets Synced

| Action in App | Synced to Supabase? | Table |
|--------------|---------------------|-------|
| Add Course | ✅ Yes | `studysync_courses` |
| Edit Course | ✅ Yes | `studysync_courses` |
| Delete Course | ✅ Yes | `studysync_courses` |
| Add Task | ✅ Yes | `studysync_tasks` |
| Edit Task | ✅ Yes | `studysync_tasks` |
| Delete Task | ✅ Yes | `studysync_tasks` |
| Toggle Task Complete | ✅ Yes | `studysync_tasks` |
| Add Study Session | ✅ Yes | `studysync_study_sessions` |

---

## Testing

### Test 1: Add a Course

1. **Run your app**
2. **Add a course** (e.g., "Math 101")
3. **Check Supabase Dashboard:**
   - Go to **Table Editor** → `studysync_courses`
   - You should see your course! ✅

### Test 2: Add a Task

1. **In your app**, navigate to a course
2. **Add a task** (e.g., "Complete Assignment 1")
3. **Check Supabase Dashboard:**
   - Go to **Table Editor** → `studysync_tasks`
   - You should see your task! ✅

---

## Key Features

### ✅ Local First
- **Room Database** saves data locally first (instant, works offline)
- App works fully offline - no internet needed!

### ✅ Automatic Sync
- **Supabase sync** happens automatically in background
- You don't need to do anything - it just works!

### ✅ Error Handling
- If Supabase sync fails, **app still works** (data is in Room)
- Errors are logged but don't crash the app
- Sync retries on next data change

### ✅ Offline Support
- App works **fully offline** (Room database)
- When online, sync happens automatically
- No data loss if sync fails

---

## Key Files

- **`SupabaseClient.kt`** - Connects to Supabase
- **`SupabaseSyncService.kt`** - Syncs data to/from Supabase
- **`TaskRepository.kt`** - Saves tasks (Room + Supabase)
- **`CourseRepository.kt`** - Saves courses (Room + Supabase)
- **`StudySessionRepository.kt`** - Saves sessions (Room + Supabase)
- **`local.properties`** - Your Supabase credentials (not in git)
- **`supabase/migrations/...sql`** - SQL to create tables

---

## Troubleshooting

**Data not appearing?**
1. Check **Logcat** (filter by `TaskRepository`, `SupabaseSyncService`)
2. Verify `local.properties` has correct credentials
3. Check Supabase Dashboard → **Table Editor** to see if tables exist
4. Ensure device has internet connection

---

## Important Notes

- **Tables use `studysync_` prefix** - Keeps them separate from flask-app tables
- **Anon key is safe** - Stored in `local.properties` (not in git)
- **Migration SQL** - Run once to create tables, then app handles everything automatically

---

## Summary

1. **Setup once:** Add credentials to `local.properties`, create tables in Supabase
2. **Use app normally:** Add courses, tasks, sessions
3. **Data syncs automatically:** Room → Supabase (background)
4. **Check Supabase Dashboard:** See your data in the cloud!

**That's it!** Simple and works automatically. 🎉
