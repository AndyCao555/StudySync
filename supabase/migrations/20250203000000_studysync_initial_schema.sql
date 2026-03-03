-- ================================================================
-- StudySync Android App - Initial Schema
-- ================================================================
-- Separate tables from flask-app (profiles, collected_messages, reports)
-- All tables use studysync_ prefix to avoid conflicts

-- ----------------------------------------------------------------
-- Courses
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.studysync_courses (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    color_hex TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- Tasks
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.studysync_tasks (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES studysync_courses(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    due_date DATE,
    priority INTEGER NOT NULL DEFAULT 3,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- Study Sessions
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.studysync_study_sessions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES studysync_courses(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- Snapshots (for collector agent)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.studysync_snapshots (
    id TEXT PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL,
    device JSONB NOT NULL,
    aggregator JSONB NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- Indexes
-- ----------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_studysync_tasks_course_id ON studysync_tasks(course_id);
CREATE INDEX IF NOT EXISTS idx_studysync_tasks_due_date ON studysync_tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_studysync_study_sessions_course_id ON studysync_study_sessions(course_id);
CREATE INDEX IF NOT EXISTS idx_studysync_snapshots_timestamp ON studysync_snapshots(timestamp DESC);

-- ----------------------------------------------------------------
-- Row Level Security
-- ----------------------------------------------------------------
ALTER TABLE studysync_courses ENABLE ROW LEVEL SECURITY;
ALTER TABLE studysync_tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE studysync_study_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE studysync_snapshots ENABLE ROW LEVEL SECURITY;

-- Policies: Allow all operations (adjust based on your security requirements)
CREATE POLICY "Allow all on studysync_courses" ON studysync_courses
    FOR ALL USING (true) WITH CHECK (true);

CREATE POLICY "Allow all on studysync_tasks" ON studysync_tasks
    FOR ALL USING (true) WITH CHECK (true);

CREATE POLICY "Allow all on studysync_study_sessions" ON studysync_study_sessions
    FOR ALL USING (true) WITH CHECK (true);

CREATE POLICY "Allow all on studysync_snapshots" ON studysync_snapshots
    FOR ALL USING (true) WITH CHECK (true);
