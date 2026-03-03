# StudySync - Smart Study Planner App

## 📖 What Is This?

**StudySync** is an Android app that helps you organize your studies. Think of it as a digital planner that:

- **Tracks your courses** (like "Math 101" or "History 200")
- **Manages your tasks** (assignments, homework, projects)
- **Records study sessions** (how long you studied each course)
- **Works offline** - saves everything on your phone first
- **Syncs to the cloud** - automatically backs up your data online

**Simple Explanation:**
- You create courses (like subjects you're taking)
- You add tasks to each course (homework, assignments, etc.)
- You can mark tasks as complete when done
- Everything is saved on your phone AND in the cloud automatically

---

## 🚀 Getting Started - Complete Setup Guide

**Follow these steps in order to get the app working on your computer.**

### Prerequisites (What You Need First)

Before you start, make sure you have:

1. ✅ **Android Studio** installed on your computer
   - Download from: https://developer.android.com/studio
   - Install it following the setup wizard
   - Make sure Android SDK is installed (usually done automatically)

2. ✅ **A Supabase account** (free)
   - Go to: https://supabase.com
   - Sign up for a free account
   - Create a new project

3. ✅ **This project downloaded**
   - Either cloned from GitHub or downloaded as a ZIP
   - Extracted to a folder on your computer

---

## 📋 Step-by-Step Setup Instructions

### Step 1: Open the Project in Android Studio

1. **Launch Android Studio**
2. Click **"Open"** or **"File → Open"**
3. Navigate to the `StudySync` folder
4. Click **"OK"** to open the project
5. **Wait** - Android Studio will download dependencies (this may take 5-10 minutes the first time)

**What you'll see:**
- The project will appear in the left sidebar
- Android Studio will show "Gradle Sync" at the bottom
- Wait until it says "Gradle sync finished" ✅

---

### Step 2: Get Your Supabase Credentials

You need to connect the app to Supabase (the cloud database). Here's how:

1. **Go to Supabase Dashboard**
   - Open: https://supabase.com/dashboard
   - Log in to your account

2. **Select Your Project**
   - Click on your project (or create a new one if you don't have one)

3. **Get Your Credentials**
   - Click **"Settings"** (gear icon) in the left sidebar
   - Click **"API"** in the settings menu
   - You'll see two important things:
     - **Project URL** - looks like `https://xxxxx.supabase.co`
     - **anon public key** - a long string starting with `eyJ...`

4. **Copy Both Values**
   - Copy the **Project URL** (you'll need it in Step 3)
   - Copy the **anon public key** (you'll need it in Step 3)
   - Keep these somewhere safe (like a text file) for now

---

### Step 3: Add Credentials to the App

The app needs to know where to send your data. Here's how to tell it:

1. **Find the `local.properties` file**
   - In Android Studio, look at the left sidebar (Project view)
   - Find `local.properties` in the root folder (same level as `app` folder)
   - If you don't see it, look for `local.properties.example` instead

2. **Open the file**
   - Double-click `local.properties` (or `local.properties.example`)
   - If you see `local.properties.example`, you need to create `local.properties`:
     - Right-click `local.properties.example`
     - Click **"Copy"**
     - Right-click in the same folder
     - Click **"Paste"**
     - Rename the copy to `local.properties` (remove `.example`)

3. **Add your credentials**
   - The file should look like this:
     ```
     sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
     SUPABASE_URL=https://your-project-id.supabase.co
     SUPABASE_ANON_KEY=your-anon-key-here
     ```
   - Replace `https://your-project-id.supabase.co` with your **Project URL** from Step 2
   - Replace `your-anon-key-here` with your **anon public key** from Step 2
   - **Important:** Keep the quotes if they're there, or don't add quotes if they're not there
   - Save the file (Ctrl+S or Cmd+S)

**Example of what it should look like:**
```
sdk.dir=C\:\\Users\\Andy\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=https://nrdfkatusmlutijlezhc.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5yZGZrYXR1c21sdXRpamxlemhjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE4OTAyNTcsImV4cCI6MjA4NzQ2NjI1N30.468HYh8GuG9B1RzqTe8JhhKFfP7SUIPbwBpDfFjHD8E
```

---

### Step 4: Create Database Tables in Supabase

The app needs tables (like spreadsheets) to store your data. Here's how to create them:

1. **Open the SQL file**
   - In Android Studio, find the file: `supabase/migrations/20250203000000_studysync_initial_schema.sql`
   - Double-click to open it
   - **Select All** (Ctrl+A or Cmd+A)
   - **Copy** (Ctrl+C or Cmd+C)

2. **Go to Supabase SQL Editor**
   - Go back to your Supabase Dashboard
   - Click **"SQL Editor"** in the left sidebar
   - Click **"New query"** button

3. **Paste and Run**
   - **Paste** the SQL code you copied (Ctrl+V or Cmd+V)
   - Click **"Run"** button (or press F5)
   - Wait a few seconds
   - You should see: ✅ "Success. No rows returned"

**What this does:**
- Creates 4 tables in your database:
  - `studysync_courses` - for your courses
  - `studysync_tasks` - for your tasks
  - `studysync_study_sessions` - for study time tracking
  - `studysync_snapshots` - for data collection (advanced)

---

### Step 5: Sync and Build the Project

Now tell Android Studio to use your new credentials:

1. **Sync Gradle**
   - In Android Studio, click **"File"** menu
   - Click **"Sync Project with Gradle Files"**
   - Wait for it to finish (you'll see progress at the bottom)

2. **Rebuild the Project**
   - Click **"Build"** menu
   - Click **"Rebuild Project"**
   - Wait for it to finish (this may take 1-2 minutes)

**What to look for:**
- At the bottom, you should see "BUILD SUCCESSFUL" ✅
- If you see errors, check Step 3 again (credentials might be wrong)

---

### Step 6: Run the App

Now you're ready to test the app!

1. **Connect a Device or Start an Emulator**
   - **Option A - Real Phone:**
     - Connect your Android phone via USB
     - Enable "Developer Options" and "USB Debugging" on your phone
     - Your phone should appear in Android Studio
   - **Option B - Emulator:**
     - Click the device dropdown (top toolbar)
     - Click "Create Device" or select an existing emulator
     - Click the play button to start it

2. **Run the App**
   - Click the green **"Run"** button (▶️) in the toolbar
   - Or press **Shift+F10** (Windows/Linux) or **Ctrl+R** (Mac)
   - Wait for the app to install and launch

3. **Test It!**
   - The app should open on your device/emulator
   - Try creating a course
   - Try adding a task
   - Check if it appears in Supabase (see Step 7)

---

### Step 7: Verify It's Working

Make sure data is syncing to Supabase:

1. **Add Some Data in the App**
   - Create a course (e.g., "Math 101")
   - Add a task to that course (e.g., "Complete Assignment 1")

2. **Check Supabase Dashboard**
   - Go to: https://supabase.com/dashboard
   - Select your project
   - Click **"Table Editor"** in the left sidebar
   - Click on **`studysync_courses`** table
   - You should see your course! ✅
   - Click on **`studysync_tasks`** table
   - You should see your task! ✅

**If you don't see data:**
- Check Logcat in Android Studio (bottom panel)
- Filter by "TaskRepository" or "SupabaseSyncService"
- Look for error messages
- See "Troubleshooting" section below

---

## ✅ You're Done!

If you completed all 7 steps and can see data in Supabase, **everything is working!** 🎉

---

## 📱 How to Use the App

### Creating a Course

1. Open the app
2. Click **"Go to Courses"**
3. Enter a course name (e.g., "Math 101")
4. Enter a color (hex code, e.g., "#2196F3" for blue)
5. Click **"Add Course"**

### Adding a Task

1. Click on a course to open it
2. Click the **"+"** button (bottom right)
3. Fill in:
   - **Title** (required) - e.g., "Complete Assignment 1"
   - **Description** (optional)
   - **Due Date** (optional) - format: DD-MM-YYYY, e.g., "31-12-2024"
   - **Priority** (1-5, default is 3)
4. Click **"Save Task"**

### Completing a Task

1. Open a course
2. Find the task you want to mark complete
3. Click the **checkbox** next to the task
4. The task is now marked as complete ✅

### Deleting a Task

1. Open a course
2. Find the task you want to delete
3. Click the **trash icon** (🗑️) next to the task
4. The task is deleted (from both your phone and Supabase)

### Filtering Tasks

1. Open a course
2. You'll see filter buttons: **"All"**, **"Completed"**, **"Pending"**
3. Click any button to filter tasks

---

## 🔧 Troubleshooting

### Problem: "Gradle sync failed"

**Solution:**
- Check your internet connection
- In Android Studio: **File → Invalidate Caches → Invalidate and Restart**
- Try syncing again

### Problem: "Build failed" or "Cannot find BuildConfig"

**Solution:**
1. Make sure `local.properties` exists and has your credentials
2. **File → Sync Project with Gradle Files**
3. **Build → Clean Project**
4. **Build → Rebuild Project**

### Problem: App crashes when opening

**Solution:**
1. Check Logcat (bottom panel in Android Studio)
2. Look for red error messages
3. Common issues:
   - Missing INTERNET permission (should be in AndroidManifest.xml)
   - Wrong Supabase credentials
   - Tables not created in Supabase

### Problem: Data not appearing in Supabase

**Solution:**
1. Check Logcat - filter by "TaskRepository" or "SupabaseSyncService"
2. Look for error messages
3. Verify:
   - `local.properties` has correct URL and key
   - Tables exist in Supabase (check Table Editor)
   - Device/emulator has internet connection
   - You rebuilt the project after adding credentials

### Problem: "Supabase credentials not configured"

**Solution:**
1. Check `local.properties` file exists
2. Verify it has `SUPABASE_URL` and `SUPABASE_ANON_KEY` (not placeholder values)
3. **File → Sync Project with Gradle Files**
4. **Build → Rebuild Project**

---

## 📚 Understanding the App

### How Data Works

```
You add data in app
  ↓
Saved on your phone (Room Database) - Works offline!
  ↓
Automatically synced to Supabase (cloud) - Backed up online!
```

**Key Points:**
- ✅ **Works offline** - App saves everything on your phone first
- ✅ **Auto-sync** - When online, data syncs to Supabase automatically
- ✅ **No data loss** - If sync fails, data is still on your phone
- ✅ **Background sync** - Happens automatically, you don't need to do anything

### What Gets Synced

| Action in App | Synced to Supabase? |
|--------------|---------------------|
| Add Course | ✅ Yes |
| Edit Course | ✅ Yes |
| Delete Course | ✅ Yes |
| Add Task | ✅ Yes |
| Edit Task | ✅ Yes |
| Delete Task | ✅ Yes |
| Mark Task Complete | ✅ Yes |
| Add Study Session | ✅ Yes |

---

## 📁 Project Structure

```
StudySync/
├── app/                          # Main app code
│   └── src/main/java/.../
│       ├── MainActivity.kt       # Main app screen
│       └── data/                 # Data layer
│           ├── SupabaseClient.kt      # Connects to Supabase
│           ├── SupabaseSyncService.kt # Syncs data
│           ├── TaskRepository.kt       # Manages tasks
│           └── CourseRepository.kt    # Manages courses
├── supabase/
│   └── migrations/               # Database setup SQL
├── local.properties              # Your credentials (NOT in git)
├── local.properties.example      # Template (safe to commit)
└── README.md                     # This file
```

---

## 🔒 Security Notes

### What's Safe to Commit

✅ **Safe to commit to GitHub:**
- All code files (`.kt`, `.xml`, etc.)
- `local.properties.example` (template)
- `supabase/migrations/` (SQL files)
- Documentation files

❌ **NEVER commit:**
- `local.properties` (contains your actual Supabase credentials)
- `build/` folder (build artifacts)
- `.gradle/` folder (cache)

**Good news:** The `.gitignore` file is already set up to protect sensitive files!

---

## 📖 Additional Documentation

- **`DATABASE_GUIDE.md`** - Detailed guide on how the database works
- **`HOW_TO_VIEW_DATA_IN_SUPABASE.md`** - How to view your data in Supabase dashboard

---

## 🆘 Need Help?

If you're stuck:

1. **Check the error message** in Android Studio (Logcat)
2. **Read the Troubleshooting section** above
3. **Check Supabase Dashboard** - verify tables exist and credentials are correct
4. **Verify all 7 setup steps** were completed correctly

---

## 🎉 Summary

**StudySync** is a study planner app that:
- ✅ Organizes your courses and tasks
- ✅ Works offline (saves on your phone)
- ✅ Syncs to the cloud automatically (Supabase)
- ✅ Simple to use
- ✅ Free to use

**To get started:**
1. Open project in Android Studio
2. Add Supabase credentials to `local.properties`
3. Create tables in Supabase
4. Sync and rebuild
5. Run the app!

**That's it!** Happy studying! 📚
