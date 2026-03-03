# How to View Your Data in Supabase

## Quick Steps

1. **Go to Supabase Dashboard**
   - Visit [https://supabase.com/dashboard](https://supabase.com/dashboard)
   - Select your project

2. **Open Table Editor**
   - Click **Table Editor** in the left sidebar
   - You'll see all your tables listed

3. **View Your Data**
   - Click on the table name to see its data:
     - `studysync_courses` - Your courses
     - `studysync_tasks` - Your tasks
     - `studysync_study_sessions` - Your study sessions

---

## Step-by-Step with Screenshots

### Step 1: Open Table Editor

1. In Supabase Dashboard, look at the **left sidebar**
2. Find **"Table Editor"** (usually has a table icon)
3. Click on it

### Step 2: Select Your Table

You'll see a list of tables. Look for:
- ✅ `studysync_courses` - Click this to see courses
- ✅ `studysync_tasks` - Click this to see tasks
- ✅ `studysync_study_sessions` - Click this to see study sessions

**Note:** You might also see other tables like `devices`, `snapshots`, `profiles`, etc. - those are from your flask-app, ignore them.

### Step 3: View Your Data

Once you click on a table (e.g., `studysync_tasks`), you'll see:
- **Rows of data** - Each row is a task you created
- **Columns** - id, course_id, title, description, due_date, priority, is_completed, etc.
- **Refresh button** - Click to see latest data

---

## What You Should See

### For Tasks (`studysync_tasks`):

| id | course_id | title | description | due_date | priority | is_completed |
|----|-----------|-------|-------------|----------|----------|-------------|
| 1 | 1 | Complete Assignment 1 | Finish calculus problems | 2024-12-20 | 3 | false |
| 2 | 1 | Study for Exam | Review chapters 1-5 | 2024-12-25 | 5 | false |

### For Courses (`studysync_courses`):

| id | name | color_hex |
|----|------|-----------|
| 1 | Math 101 | #2196F3 |
| 2 | Science 202 | #4CAF50 |

---

## Troubleshooting

### Don't See Any Data?

**Check:**
1. **Did you add data in the app?** - Make sure you actually created a course/task
2. **Is the app syncing?** - Check Logcat for errors (filter by `CourseRepository`, `TaskRepository`)
3. **Refresh the table** - Click the refresh button in Table Editor
4. **Check the right table** - Make sure you're looking at `studysync_tasks`, not `tasks`

### Data Not Appearing?

**Possible causes:**
1. **Supabase sync failed** - Check Logcat for "Supabase sync failed" errors
2. **Network issue** - Make sure device has internet
3. **Credentials wrong** - Verify `local.properties` has correct URL and key
4. **Tables don't exist** - Make sure you ran the migration SQL

**To check:**
- Look at **Supabase Dashboard → Logs → API Logs** for errors
- Check Android Logcat for sync errors

---

## Alternative: Using SQL Editor

You can also view data using SQL:

1. Go to **SQL Editor** (left sidebar)
2. Run this query:
   ```sql
   SELECT * FROM studysync_tasks;
   ```
3. Click **Run**
4. See all your tasks!

---

## Quick Reference

| What You Want | Where to Look |
|--------------|---------------|
| Courses | Table Editor → `studysync_courses` |
| Tasks | Table Editor → `studysync_tasks` |
| Study Sessions | Table Editor → `studysync_study_sessions` |
| Check for errors | Logs → API Logs |
| Run custom queries | SQL Editor |

---

**That's it!** Your data should be visible in Table Editor. 🎉
